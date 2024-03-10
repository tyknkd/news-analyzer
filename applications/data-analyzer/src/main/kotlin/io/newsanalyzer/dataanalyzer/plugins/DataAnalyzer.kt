package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.spark.api.*
//import org.apache.spark.sql.*
import org.apache.spark.ml.feature.RegexTokenizer
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.ml.clustering.LDA
import org.apache.spark.ml.linalg.DenseVector
//import org.jetbrains.kotlinx.spark.api.SparkSession

class DataAnalyzer {
    private suspend fun getData(): List<Article> {
        val port = System.getenv("COLLECTOR_PORT")
        val apiHost = if (System.getenv("OS_ENV") == "container") {
            "data-collector:$port"
        } else {
            "localhost:$port"
        }
        val path = "articles"
        val client = HttpClient(engineFactory = Java) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTP
                host = apiHost
                path(path)
            }
        }
        client.close()
        val articleList: List<Article> = response.body()
        return articleList
    }

    suspend fun extractTopics(): TopicData {
        val articleList = getData()
        val articleKtDf: DataFrame<Article> = articleList.toDataFrame()

        // Combine text strings, exclude HTML tags
        // and "… [+1234 chars]" substrings at the end of content string
        val htmlCharsRegEx = Regex("<[^>]*>|\\w*… \\[\\+\\d+ chars\\]")
        val textKtDf = articleKtDf.add("text") {
            ("title"<String>() + " " + "description"<String>() +  " " + "content"<String>())
                .replace(htmlCharsRegEx, " ")
        }.select("id", "text")
        val textList = textKtDf.toListOf<ArticleText>()

        lateinit var termList: List<String>
        lateinit var termIndicesList: List<TermIndices>
        lateinit var articleTopicList: List<ArticleTopic>

        withSpark(master = "local") {
            //  Tokenize text
            val textSparkDf = textList.toDF()
            val regExTokenizer = RegexTokenizer()
                .setInputCol("text")
                .setOutputCol("tokens")
                .setPattern("[A-Za-z]+")
                .setGaps(false)
            val tokenDf = regExTokenizer
                .transform(textSparkDf)
                .select("id", "tokens")

            // Remove stop words
            val stopWordRemover = StopWordsRemover()
                .setInputCol("tokens")
                .setOutputCol("filtered")
            val filterDf = stopWordRemover
                .transform(tokenDf)
                .select("id","filtered")

            // Vectorize frequency count
            val vectorizer = CountVectorizer()
                .setInputCol("filtered")
                .setOutputCol("features")
                .fit(filterDf)
            val featureDf = vectorizer
                .transform(filterDf)
                .select("id","features")
            termList = vectorizer.vocabulary().toList()

            // Latent Dirichlet Allocation
            val numberTopics = 10
            val ldaModel = LDA()
                .setK(numberTopics)
                .setOptimizer("em") // Expectation maximization
                .fit(featureDf)
            val tokensPerTopic = 5
            val termIndicesDf = ldaModel.describeTopics(tokensPerTopic)
            termIndicesList = termIndicesDf.toList<TermIndices>()
            val topicDistDf = ldaModel
                .transform(featureDf)
                .select("id", "topicDistribution")

            // For each article, assign topic with maximum probability
            val argMax = udf {
                vec: DenseVector -> vec.argmax()
            }
            udf.register("argMax", argMax)
            val articleTopicDf = topicDistDf
                .withColumn("topicId",
                    argMax(col("topicDistribution")))
                .select("id","topicId")
            articleTopicList = articleTopicDf.toList<ArticleTopic>()

        }
        return TopicData(articleKtDf, termList, termIndicesList, articleTopicList)
    }

    suspend fun articleTopics(): List<AnalyzedArticle> {
        val (articleDf, termList, termIndicesList, articleTopicList) = extractTopics()
        val termIndicesDf = termIndicesList.toDataFrame()

        // Replace term indices with term strings
        val topicTermsDf = termIndicesDf.add("terms") {
            "termIndices"<List<Int>>().map {
                termList.get(it)
            }
        }.select("topic","terms")

        // Join article topic data with topic terms data
        val articleTopicDf = articleTopicList.toDataFrame()
        val articleTopicTermsDf = articleTopicDf.leftJoinWith(topicTermsDf) {
            right.getValue<Int>("topic") == "topicId"<Int>()
        }.select("id","topicId","terms")

        // Combine with original article data
        val analyzedArticleDf = articleDf.leftJoinWith(articleTopicTermsDf) {
            right.getValue<Int>("id") == "id"<Int>()
        }.remove("id1")

        return analyzedArticleDf.toListOf<AnalyzedArticle>()
    }
}

val dataAnalyzer = DataAnalyzer()
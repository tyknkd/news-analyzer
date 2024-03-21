package io.newsanalyzer.dataanalyzer.plugins

import io.newsanalyzer.dataanalyzer.models.*
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.spark.api.*
import org.apache.spark.ml.feature.RegexTokenizer
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.ml.clustering.LDA
import org.apache.spark.ml.linalg.DenseVector

object DataAnalyzer {
    private fun extractTopics(articleList: List<Article>): TopicData {
        val numberArticles = articleList.size

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
                .setStopWords(StopWords.stopWords)
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
            val numberTopics = numberArticles/10
            val ldaModel = LDA()
                .setK(numberTopics)
                .setOptimizer("em") // Expectation maximization
                .fit(featureDf)
            val tokensPerTopic = 10
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

    private fun getArticleTopics(articleList: List<Article>): Pair<List<Article>,List<Topic>> {
        val (articleDf, termList, termIndicesList, articleTopicList) = extractTopics(articleList)
        val termIndicesDf = termIndicesList.toDataFrame()

        // Replace term indices with term strings
        val topicTermsDf = termIndicesDf
            .add("termsList") {
            "termIndices"<List<Int>>().map {
                termList[it]
            }
        }.select("topic","termsList")

        // Convert list of term strings to string
        val topicTermsStringDf = topicTermsDf
            .rename("topic").into("topicId")
            .add("terms") {
                "termsList"<List<String>>().toString()
            }.select("topicId","terms")

        // Update article topics
        val articleTopicDf = articleTopicList.toDataFrame()
        val analyzedArticleDf = articleDf.remove("topicId").leftJoinWith(articleTopicDf) {
            right.getValue<Int>("id") == "id"<Int>()
        }.remove("id1")

        return Pair(analyzedArticleDf.toListOf<Article>(), topicTermsStringDf.toListOf<Topic>())
    }

    suspend fun getAnalyzedData(): Pair<List<Article>,List<Topic>> {
        val articles = CollectedDataClient.getCollectedData()
        return getArticleTopics(articles)
    }
}
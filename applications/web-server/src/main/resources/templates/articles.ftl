<#-- @ftlvariable name="articlesOnTopic" type="io.newsanalyzer.webserver.models.ArticlesByTopic" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h3 class="topicHeader">
        <a href="/topics">
            <span class="topicId">Topic Group ${articlesOnTopic.topic.topicId} • </span>
            <span class="keywords">Keywords: ${articlesOnTopic.topic.terms?keep_after("[")?keep_before_last("]")?capitalize}</span>
        </a>
    </h3>
    <ul>
        <#list articlesOnTopic.articles as article>
            <li>
                <h4>
                    <span class="title"><a href="${article.url}">${article.title}</a></span>
                </h4>
                <p>
                    <span class="publisher">${article.publisher}, </span>
                    <span class="date">${article.publishedAt.toString()?string?datetime.iso}</span>
                </p>
            </li>
        </#list>
    </ul>
</@layout.header>
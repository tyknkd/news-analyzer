<#-- @ftlvariable name="articlesByTopic" type="kotlin.collections.List<io.newsanalyzer.webserver.models.ArticlesByTopic>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <ul>
        <#list articlesByTopic as group>
            <li>
                <h3 class="topicHeader">
                    <span class="topicId">Topic Group ${group.topic.topicId + 1} • </span>
                    <span class="keywords">Keywords: ${group.topic.terms?keep_after("[")?keep_before_last("]")?capitalize}</span>
                </h3>
                <ul>
                    <#list group.articles as article>
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
            </li>
            <hr>
        </#list>
    </ul>
</@layout.header>
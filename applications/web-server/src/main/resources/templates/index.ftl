<#-- @ftlvariable name="articlesByTopic" type="kotlin.collections.List<io.newsanalyzer.webserver.models.ArticlesByTopic>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <ul>
        <#list articlesByTopic as group>
            <li>
                <h3 class="topicHeader">
                    <a href="/topics/${group.topic.topicId}/articles">
                        <span class="topicId">Topic Group ${group.topic.topicId} • </span>
                        <span class="keywords">Keywords: ${group.topic.terms?keep_after("[")?keep_before_last("]")?capitalize}</span>
                    </a>
                </h3>
                <ul>
                    <#list group.articles[0..*3] as article>
                        <li>
                            <h4>
                                <span class="title"><a target="_blank" rel="noopener" href="${article.url}">${article.title}</a></span>
                            </h4>
                            <p>
                                <span class="publisher">${article.publisher}, </span>
                                <span class="date">${article.publishedAt.toString()?string?datetime.iso}</span>
                            </p>
                        </li>
                    </#list>
                    <li>
                        <h4>
                            <a href="/topics/${group.topic.topicId}/articles">More on this topic . . .</a>
                        </h4>
                    </li>
                </ul>
            </li>
            <hr>
        </#list>
    </ul>
</@layout.header>
<#-- @ftlvariable name="topics" type="kotlin.collections.List<io.newsanalyzer.datasupport.models.Topic>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <ul>
        <#list topics as topic>
            <li>
                <h3 class="topicHeader">
                    <a href="/topics/${topic.topicId}/articles">
                        <span class="topicId">Topic Group ${topic.topicId} • </span>
                        <span class="keywords">Keywords: ${topic.terms?keep_after("[")?keep_before_last("]")?capitalize}</span>
                    </a>
                </h3>
            </li>
        </#list>
    </ul>
</@layout.header>
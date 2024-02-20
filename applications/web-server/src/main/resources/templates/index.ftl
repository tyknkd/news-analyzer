<#-- @ftlvariable name="articles" type="kotlin.collections.List<io.newsanalyzer.webserver.models.Article>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <#list articles?reverse as article>
        <h3>
            <a href="/articles/${article.id}">${article.title}</a>
        </h3>
        <p>
            ${article.body}
        </p>
    </#list>
</@layout.header>
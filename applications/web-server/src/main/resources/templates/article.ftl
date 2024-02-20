<#-- @ftlvariable name="article" type="io.newsanalyzer.webserver.models.Article" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <h3>
        ${article.title}
    </h3>
    <p>
        ${article.body}
    </p>
</@layout.header>
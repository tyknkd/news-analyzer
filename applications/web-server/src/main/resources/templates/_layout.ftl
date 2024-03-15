<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="/styles/style.css">
        <title>Tech Industry News Analyzer</title>
    </head>
    <body>
        <header>
            <h1><a href="/">Tech Industry News Analyzer</a></h1>
            <h2><a href="/topics">Topics</a> • <a href="/">Articles</a> • <a href="/about">About</a> • <a href="/api">API</a></h2>
            <hr>
        </header>
        <#nested>
    </body>
    <footer>
        <p>&copy; ${.now?string('yyyy')} <a target="_blank" rel="noopener" href="https://tyknkd.github.io">Tyler Kinkade</a>, All Rights Reserved</p>
    </footer>
    </html>
</#macro>
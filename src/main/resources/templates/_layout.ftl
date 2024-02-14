<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="/styles/style.css">
        <title>News Analyzer</title>
    </head>
    <body>
    <h1><a href="/">News Analyzer</a></h1>
    <#nested>
    </body>
    <footer>
        <p>&copy; ${.now?string('yyyy')} <a target="_blank" rel="noopener" href="https://tyknkd.github.io">Tyler Kinkade</a>, All Rights Reserved</p>
    </footer>
    </html>
</#macro>
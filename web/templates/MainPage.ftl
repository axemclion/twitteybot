<html>
    <head>
        <title>TwitteyBot :: Managing twitter bots made easy</title>
        <link rel="stylesheet" type=text/css href="/css/main.css"/>
    </head>
    <body>
        <div class = "header">
            <#if username??>
            ${username}<#else><a href = "${loginUrl}">Login</a>
            </#if> 
        </div>
        <div class = "content">
            <div class = "twitter-account-pane">
            <div class = "twitter-accounts">
                <ul>
                    <#list accounts as item>
                    <li>
                        <a href = "${twitterPage}action=edit&username=${item}"> ${item}</a>
                        <a href="${twitterPage}action=delete&username=${item}">[delete]</a>
                    </li>
                    </#list>
                </ul>
            </div>
            <div id = "addTwitterAccount">
                <a href = "${twitterPage}">Add a new account</a>
            </div>
            <div class = "footer"></div>
            </body>
            <script src="http://yui.yahooapis.com/3.0.0pr2/build/yui/yui-min.js" type="text/javascript"></script>
            <script src="/js/TwitteyBot.js" type="text/javascript"></script>
        </script>
        </html>

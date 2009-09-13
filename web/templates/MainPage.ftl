<html>
    <head>
        <title>TwitteyBot :: Managing twitter bots made easy</title>
        <link rel="stylesheet" type=text/css href="/css/main.css"/>
    </head>
    <body>
        <div class = "header">${username!""} | (<a href = "${logoutUrl}">logout</a>)</div>
        <div class = "content">
			<div class = "twitter-accounts">
			    <ul>
			        <#list accounts as item>
			        <li>
			            ${item.twitterScreenName}<a href="/pages/manageTwitterAccount?action=delete&screenName=${item.twitterScreenName}">[delete]</a>
			            |<a href = "#">Upload File</a> | <a href = "/pages/status?action=show&screenName=${item.twitterScreenName}">Show</a>
			            <form method = "POST" action = "/pages/status?action=upload&screenName=${item.twitterScreenName}" enctype='multipart/form-data'>
			            	<input type = "file" name = "filename"/>
			            	<input type = "submit"/>
			            </form>
			        </li>
			        </#list>
			    </ul>
			</div>
			<div id = "addTwitterAccount">
			    <a href = "/pages/manageTwitterAccount?action=add" target = "_blank">Add a new account</a>
			</div>
		</div>
        <div class = "footer"></div>
    </body>
    <script src="http://yui.yahooapis.com/3.0.0pr2/build/yui/yui-min.js" type="text/javascript"></script>
    <script src="/js/TwitteyBot.js" type="text/javascript"></script>
    </script>
</html>

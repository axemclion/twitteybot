<html>
	<head>
		<title>
			TwitteyBot :: Managing twitter bots made easy
		</title>
		<script src="http://yui.yahooapis.com/3.0.0pr2/build/yui/yui-min.js" type="text/javascript"></script>
		<link rel="stylesheet" type=text/css href="/css/main.css"/> 
	</head>
    <body>
        <div class = "header">
		<#if username??>
		  ${username}
		<#else>
		  <a href = "${loginUrl}">Login</a>
		</#if>  
        </div>
        <div class = "content">
            <div class = "twitter-account-pane">
				<div class = "twitter-accounts">
					<#list accounts as item>
						${item}
					</#list>
            	</div>
				<div class = "add-twitter-account">
					Add a new account
				<div>
			</div>
			
        </div>
        <div class = "footer"></div>
    </body>
	
</html>

<html>
<head>
    <title>TwitteyBot :: Managing twitter bots made easy</title>
    <link rel="stylesheet" type=text/css href="/css/main.css"/>
</head>
<body>
    <div id = "header">
        <div class = "left-pane">
        </div>
        <div class = "right-pane" style ="text-align:right">
            <ul>
                <li>
                    <strong>${username!""}</strong>
                    | 
                </li>
                <li>
                    <a href = "/help.html">Help</a>
                    | 
                </li>
                <li>
                    <a href = "${logoutUrl}">Sign Out</a>
                    | 
                </li>
            </ul>
        </div>
        <div class = "clear">
        </div>
    </div>
    <div id = "content">
        <div id = "addTwitterAccount">
            <a href = "/pages/manageTwitterAccount?action=add">Add a new account</a>
        </div>
        <div class = "left-pane" style ="border-right : SOLID #E5ECF9 3px">
            <ul id = "twitterAccountList">
                <#list accounts as item>
                <li>
                    <a href = "/pages/status?action=show&screenName=${item.twitterScreenName}">${item.twitterScreenName}</a>
                </li>
                </#list>
            </ul>
        </div>
        <div class = "right-pane">
            <div id = "message">
            </div>
            <div id = "twitterAccount">
                <div class = "actionBar">
                    <div class = "left-pane">
                        <strong><span id = "twitterScreenName">Not Loaded</span></strong>
                    </div>
                    <div class = "right-pane">
                        <ul id = "actionList">
                            <li>
                                <a href = "#deleteAccountForm">Delete Account </a>| 
                            </li>
                            <li>
                                <a href = "#uploadFileForm">Upload File </a>
                            </li>
                        </ul>
                        <div id = "uploadFileForm">
                            <form action = "/pages/status" method = "POST" name = "uploadFile">
                                <input type = "hidden" name = "action" type = "upload" /><input type = "hidden" name = "screenName" value = ""/><input type = "file" name = "fileName" class = "button"/><input type = "submit" value = "Upload" class = "button"/><input type = "reset" value = "Cancel" class = "button"/>
                            </form>
                        </div>
                        <div id = "deleteAccountForm">
                            <form action = "/pages/status" method = "POST">
                                Are you sure you want to delete this account ?<input type = "hidden" name = "action" value = "delete"/><input type = "hidden" name = "screenName" value = ""/><input type = "submit" value = "Yes" class = "button"/><input type = "reset" value = "No" class = "button"/>
                            </form>
                        </div>
                    </div>
                    <div class = "clear">
                    </div>
                </div>
                <div id = "toolbar">
                    <div class = "left-pane">
                        Select<a href = "#" id = "selectAllStatus">All</a>,<a href = "#" id = "selectNoneStatus">None</a>
                    </div>
                    <div class = "right-pane">
                    </div>
                </div>
                <div id = "twitterStatus">
                </div>
            </div>
        </div>
        <div class = "clear">
        </div>
    </div>
    <div id = "footer">
    </div>
</body>
<script src = "http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type = "text/javascript">
</script>
<script src="/js/TwitteyBot.js" type="text/javascript">
</script>
</script>
</html>

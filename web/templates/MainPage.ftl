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
            <ul class = "single-line-list">
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
                            <form target = "uploadedFileFrame" action = "#" method = "POST" name = "uploadFile" encType="multipart/form-data">
                                <input type = "file" name = "fileName" class = "button"/>&nbsp;<input type = "submit" value = "Upload" class = "button"/>&nbsp;<input type = "reset" value = "Cancel" class = "button"/>
                            </form>
                            <iframe name = "uploadedFileFrame" style ="display:none">
                            </iframe>
                        </div>
                        <div id = "deleteAccountForm">
                            <form action = "/pages/status" method = "POST">
                                Are you sure you want to delete this account ?<input type = "hidden" name = "action" value = "delete"/>&nbsp;<input type = "hidden" name = "screenName" value = ""/><input type = "submit" value = "Yes" class = "button"/><input type = "reset" value = "No" class = "button"/>
                            </form>
                        </div>
                    </div>
                    <div class = "clear">
                    </div>
                </div>
                <div id = "twitterContent">
                    <form name = "updateForm" method = "POST" action = "/pages/status">
                        <div id = "toolbar">
                            <div class = "left-pane">
                                Select&nbsp;
                                <ul class = "single-line-list">
                                    <li>
                                        <a href = "#" id = "selectAllStatus">All</a>, 
                                    </li>
                                    <li>
                                        <a href = "#" id = "selectNoneStatus">None</a>
                                    </li>
                                </ul>
                            </div>
                            <div class = "right-pane" style ="text-align : right">
                                <div id = "otherButtons">
                                	<input type = "Submit" name = "action" value = "update" class = "button"/>&nbsp;<input type = "submit" name = "action" value = "delete" class = "button"/>
								</div>
								<div id = "uploadButtons">
									<input type = "Submit" name = "action" value = "add" class = "button">&nbsp;<input type = "reset" value = "cancel" class = "button"/>
								</div>
                            </div>
                            <div class = "clear">
                            </div>
                        </div>
                        <div id = "twitterStatus">
                        </div>
                    </form>
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

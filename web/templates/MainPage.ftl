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
                <div class = "left-pane">
                    <a href = "/pages/manageTwitterAccount?action=Add">Link a Twitter account</a>
                </div>
                <div class = "right-pane" style ="text-align:center">
                    <span id = "message"></span>
                </div>
                <div class = "clear">
                </div>
            </div>
            <div class = "left-pane" style ="border-right : SOLID #E5ECF9 3px;height : 80%;">
                <ul id = "twitterAccountList">
                    <#list accounts as item>
                    <li>
                        <a href = "/pages/status?action=show&screenName=${item.twitterScreenName}">${item.twitterScreenName}</a>
                    </li>
                    </#list>
                </ul>
                <div id = "scheduler">
                    <div>
                        Tweet Scheduler
                    </div>
                    <form>
                        Start at&nbsp;<span id = "scheduleStart"><a href= "#" display : none></a><input type = "text" style ="width : 10em"/></span>&nbsp;and
                        <table>
                            <tr>
                                <td>
                                    <input type = "radio" name = "betweenOptions" value = "interval	"/>
                                </td>
                                <td>
                                    send tweets in intervals of <input type = "text" id = "scheduleInterval" style ="width:90%"/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <input type = "radio" name = "betweenOptions" value = "span"/>
                                </td>
                                <td>
                                    send all tweets within a time limit of 
                                    <br/>
                                    <input id = "scheduleSpan" style ="width:90%" type = "text"/>
                                </td>
                            </tr>
                        </table>
                        <center>
                            <input type = "Submit" value = "Schedule Tweets" />
                        </center>
                    </form>
                </div>
            </div>
            <div class = "right-pane">
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
                                <form target = "resultFrame" action = "#" method = "POST" name = "uploadFile" encType="multipart/form-data">
                                    <input type = "file" name = "fileName" class = "button"/>&nbsp;<input type = "submit" value = "Upload" class = "button"/>&nbsp;<input type = "reset" value = "Cancel" class = "button"/>
                                </form>
                            </div>
                            <div id = "deleteAccountForm">
                                <form action = "#/pages/manageTwitterAccount" method = "POST" name = "deleteTwitterAccount">
                                    Are you sure you want to delete this account ?<input type = "submit" value = "Yes" class = "button"/>&nbsp;<input type = "reset" value = "No" class = "button"/>
                                </form>
                            </div>
                        </div>
                        <div class = "clear">
                        </div>
                    </div>
                    <div id = "twitterContent" class = "content-window">
                        <form name = "updateForm" method = "POST" action = "/pages/status" target = "resultFrame">
                            <input name = "screenName" type = "hidden" id = "screenName"/>
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
                                        <input type = "Submit" name = "action" value = "Update" class = "button"/>&nbsp;<input type = "submit" name = "action" value = "Delete" class = "button"/>
                                    </div>
                                    <div id = "uploadButtons">
                                        <input type = "Submit" name = "action" value = "Add" class = "button">&nbsp;<input type = "reset" value = "Cancel" class = "button"/>
                                    </div>
                                </div>
                                <div class = "clear">
                                </div>
                            </div>
                            <div id = "twitterStatus">
                            </div>
                        </form>
                    </div>
                    <div id = "noTweets" class = "content-window">
                        To schedule tweeting to this account, please upload a text file. 
                        <br/>
                        Every line in the file will be sent as a tweet at the scheduled time. 
                    </div>
                </div>
            </div>
            <div class = "clear">
            </div>
        </div>
        <div id = "footer">
        </div>
        <iframe id = "resultFrame" name = "resultFrame" style ="display:none">
        </iframe>
    </body>
    <script src = "http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type = "text/javascript">
    </script>
    <script src = "/js/date.js" type = "text/javascript">
    </script>
    <script src="/js/TwitteyBot.js" type="text/javascript">
    </script>
</html>

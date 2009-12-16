<html>
    <head>
        <title>Time2Tweet :: Schedule your tweets</title>
        <link rel="stylesheet" type=text/css href="/css/main.css"/>
    </head>
    <body>
        <div id = "header">
            <div class = "left-pane" style ="margin-left : 2em; width : auto; background:url('/images/logo.png') no-repeat">
                <img class = "logo-text" src = "/images/twitter-logo.png" alt = "TweetLater">
                </img>
            </div>
            <div class = "right-pane" style ="width : 70%;text-align:right; margin-top : 25px">
                <div class = "single-line-list">
                    <span>
                        <strong>${username!""}</strong>
                        | 
                    </span>
                    <span>
                        <a href = "/help.html">Help</a>
                        | 
                    </span>
                    <span>
                        <a href = "${logoutUrl}">Sign Out</a>
                        | 
                    </span>
                </div>
            </div>
            <div class = "clear">
            </div>
        </div>
        <div id = "content">
            <div id = "addTwitterAccount">
                <div class = "left-pane">
                    <a href = "/pages/manageTwitterAccount?action=Add">Manage a new twitter account</a>
                </div>
                <div class = "right-pane" style ="text-align:center">
                    <span id = "message">
                    </span>
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
                <div id = "scheduler" class = "side-window">
                    <div>
                        Tweet Scheduler
                    </div>
                    <form>
                        Start at&nbsp;
                        <span id = "scheduleStart">
                            <a href= "#" display : none></a>
                            <input type = "text" style ="width : 10em" value = "today at 10:10"/>
                        </span>
                        &nbsp;and
                        <table>
                            <tr>
                                <td>
                                    <input checked = "true" type = "radio" name = "betweenOptions" value = "scheduleInterval"/>
                                </td>
                                <td>
                                    send tweets in intervals of 
                                    <input type = "text" id = "scheduleInterval" style ="width:90%" value = "1 hour and 10 minutes"/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <input type = "radio" name = "betweenOptions" value = "scheduleSpan"/>
                                </td>
                                <td>
                                    send all tweets within a time limit of 
                                    <br/>
                                    <input id = "scheduleSpan" style ="width:90%" type = "text" value = "1 day, 10 hours"/>
                                </td>
                            </tr>
                        </table>
                        <center>
                            <input type = "Submit" value = "Schedule Tweets" />
                        </center>
                    </form>
                </div>
                <div id = "shrinker" class = "side-window">
                    <div>
                        Tweet Shrinker
                    </div>
                    For tweets that are greater than 140 characters, 
                    <center>
                        <input type = "button" value = "Remove Vowels" id = "shrinker-removeVowels" style ="width : 80%"/>
                        <input type = "button" value = "Break into more Tweets" id = "shrinker-wrapTweet" style ="width:80%"/>
                    </center>
                </div>
            </div>
            <div class = "right-pane">
                <div id = "twitterAccount">
                    <div class = "actionBar">
                        <div class = "left-pane" style ="width : 35%">
                            <strong>
                                <span id = "twitterScreenName">
                                    Not Loaded
                                </span>
                            </strong>&nbsp;|&nbsp;<a href = "#deleteAccountForm" id = "deleteAccountButton">Un-manage Account </a>
                        </div>
                        <div class = "right-pane" style ="width : 64%">
                            <ul id = "actionList">
                                <li>
                                    Upload Tweets from a file on
                                </li>
                                <li>
                                    <a href = "#fetchFileForm">the internet </a>
                                </li>
                                <li>
                                    &nbsp;|&nbsp;<a href = "#uploadFileForm"> your computer </a>
                                </li>
                            </ul>
                            <div id = "fetchFileForm">
                                <form target = "resultFrame" action = "#" method = "POST" name = "uploadFile">
                                    <input type = "text" name = "source_" class = "button" value = "http://time2tweet.appspot.com/tweets.txt"/>
                                    &nbsp;
                                    <input type = "submit" value = "Upload" class = "button"/>
                                    &nbsp;
                                    <input type = "reset" value = "Cancel" class = "button"/>
                                </form>
                            </div>
                            <div id = "uploadFileForm">
                                <form target = "resultFrame" action = "#" method = "POST" name = "uploadFile" encType="multipart/form-data">
                                    <input type = "file" name = "fileName" class = "button"/>
                                    &nbsp;
                                    <input type = "checkbox" id = "csvFile"/> CSV File
									&nbsp;
                                    <input type = "submit" value = "Upload" class = "button"/>
                                    &nbsp;
                                    <input type = "reset" value = "Cancel" class = "button"/>
                                </form>
                            </div>
                            <div id = "deleteAccountForm">
                                <form action = "#/pages/manageTwitterAccount" method = "POST" name = "deleteTwitterAccount">
                                    Are you sure you want to unlink this account ?
                                    <input type = "submit" value = "Yes" class = "button"/>
                                    &nbsp;
                                    <input type = "reset" value = "No" class = "button"/>
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
                                    <div class = "single-line-list" style ="text-align:left">
                                        <span>
                                            <a href = "#" id = "selectAllStatus">All</a>, 
                                        </span>
                                        <span>
                                            <a href = "#" id = "selectNoneStatus">None</a>
                                        </span>
                                    </div>
                                </div>
                                <div class = "right-pane" style ="text-align : right">
                                    <div id = "otherButtons">
                                        <input type = "Submit" name = "action" value = "Update" class = "button"/>
                                        &nbsp;
                                        <input type = "submit" name = "action" value = "Delete" class = "button"/>
                                    </div>
                                    <div id = "uploadButtons">
                                        <input type = "Submit" name = "action" value = "Add" class = "button">
                                        &nbsp;
                                        <input type = "reset" value = "Cancel" class = "button"/>
                                    </div>
                                </div>
                                <div class = "clear">
                                </div>
                            </div>
                            <div id = "twitterStatus">
                            </div>
                        </form>
                    </div>
                    <div id = "showLoading" style ="display:none" class ="content-window">
                        <div style ="text-align:center; padding : 3em">
                            <span style ="font-weight : bold; font-size : 3em; color : white; text-shadow : 0px 0px 20px  #000000">
                                Loading
                            </span>
                            <br/>
                            <br/>
                            <img src = "/images/loading.gif"/>
                        </div>
                    </div>
                    <div id = "noTweets" class = "content-window">
                        To start scheduling tweets, please upload a file. Use the links above.
                        <br/>
                        <sub>
                            Every line in the file is a tweet.
                        </sub>
                        <br/>
                        <br/>
                        Or
                        <br/>
                        <a style ="display : block; font-size : 1.3em; padding : 0.3em; background-color : #EEEEEE; -moz-border-radius : 5px" href = "#" id = "sampleTweets">Show me some sample tweets.</a>
                    </div>
                </div>
            </div>
            <div class = "clear">
            </div>
        </div>
        <div id = "footer">
        </div>
        <iframe id = "resultFrame" name = "resultFrame" style ="width : 100%; display:none">
        </iframe>
    </body>
    <script src = "http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type = "text/javascript">
    </script>
    <script src = "/js/date.js" type = "text/javascript">
    </script>
    <script src="/js/TwitteyBot.js" type="text/javascript">
    </script>
</html>

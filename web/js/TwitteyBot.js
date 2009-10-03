$(document).ready(function(){
    $.extend({
        urlParser: function(url){
            var params = {};
            var separatorIndex = (url.lastIndexOf("?") > url.lastIndexOf("#")) ? url.lastIndexOf("?") : url.lastIndexOf("#");
            $.each(url.substring(separatorIndex + 1).split("&"), function(){
                var splitArray = this.split("=");
                params[splitArray[0]] = splitArray.slice(1).join("=");
            });
            return {
                "params": params
            };
        },
        parseInterval: function(interval){
            var u = {};
            u["minute"] = 1;
            u["hour"] = 60 * u["minute"];
            u["day"] = 24 * u["hour"];
            u["week"] = 7 * u["day"];
            u["month"] = 30 * u["day"];
            u["quarter"] = 3 * u["month"];
            u["year"] = 365 * u["day"];
            
            var numbers = interval.match(/[0-9]+/g);
            var words = interval.split(/[0-9]+/g);
            var result = 0;
            for (var i = 1; i < words.length; i++) {
                for (unit in u) {
                    if (words[i].indexOf(unit) !== -1) {
                        var num = parseInt(numbers[i - 1]) * u[unit];
                        if (!isNaN(num)) {
                            result += num;
                        }
                    }
                }
            }
            return result;
        },
    
    });
    
    var TwitteyBot = {
        init: function(){
            var me = this;
            if ($("#twitterAccountList>li").size() == 0) {
                $("#twitterAccount").hide();
                $("#scheduler").hide();
                this.showMessage("Add a twitter account to tweet scheduled messages to it", "warn", true);
            }
            $(".actionBar>.right-Pane>div").hide();
            $("#actionList>li>a").click(function(event){
                $(".actionBar>.right-Pane>*").hide();
                $(this.href.substring(this.href.indexOf("#"))).fadeIn();
                return false;
            });
            $(".actionBar :reset, :submit").click(function(){
                $(".actionBar>.right-Pane>*").hide();
                $(".actionBar>.right-Pane>ul").fadeIn();
            });
            
            $("#twitterAccountList a").click(function(event){
                var screenName = $.urlParser(this.href).params["screenName"];
                $("#twitterScreenName").html(screenName);
                $("#screenName").attr("value", screenName);
                me.showTweets();
                return false;
            });
            
            $("#uploadFileForm form").submit(function(){
                $(this).attr("action", "/pages/status?action=Upload&screenName=" + $("#twitterScreenName").html());
                $("#uploadButtons").show();
                $("#otherButtons").hide();
            });
            $("#deleteAccountForm form").submit(function(){
                $.get("/pages/manageTwitterAccount", {
                    "action": "delete",
                    "screenName": $("#twitterScreenName").html()
                }, function(data, status){
                    if (status !== "success") {
                        me.showMessage("Could not delete account " + $("#twitterScreenName").html(), "error");
                    }
                    else {
                        me.showMessage($("#twitterScreenName").html() + " deleted successfully. Refreshing accounts");
                        window.location.reload();
                    }
                });
                return false;
            });
            
            $("#twitterAccountList a:first").click();
            $("#twitterContent :reset").click(this.showTweets);
            
            $("#resultFrame").load(function(){
                $("#twitterStatus").html(this.contentDocument.body.innerHTML);
                me.onTweetsLoaded();
                me.showMessage($("#responseMessage").html(), $("#responseMessage").attr("title"));
            });
            $("#selectNoneStatus").click(function(){
                $("#twitterStatus .item-index").attr("checked", false);
                return false;
            });
            $("#selectAllStatus").click(function(){
                $("#twitterStatus .item-index").attr("checked", true);
                return false;
            });
            
            $("#twitterContent form").submit(function(){
                $("#uploadButtons").hide();
                $("#otherButtons").show();
            });
            
            $("#scheduleStart input[type=text]").blur(function(){
                var startDate = Date.parse(this.value);
                if (startDate !== null) {
                    $("#scheduleStart *").toggle();
                    $("#scheduleStart a").html(startDate.toString("dddd, MMMM dd, yyyy, hh:mm:ss tt (" + startDate.getTimezoneOffset() + ")"));
                    $(this).css("border", "");
                }
                else {
                    $(this).css("border", "SOLID 1px RED");
                }
            });
            $("#scheduleStart a").click(function(){
                $("#scheduleStart *").toggle();
                $("#scheduleStart input[type=text]").focus();
                return false;
            });
            
            $("textarea,input[type=text]").bind("focus", function(){
                this.select();
            });
        },
        
        showTweets: function(){
            var screenName = $("#twitterScreenName").html();
            var me = this;
            $("#twitterStatus").load("/pages/status", {
                "action": "show",
                "screenName": screenName
            }, me.onTweetsLoaded);
            $("#uploadButtons").hide();
            $("#otherButtons").show();
        },
        
        onTweetsLoaded: function(){
            if ($("#twitterContent .tweetLine").size() === 0) {
                $("#twitterContent").hide();
                $("#noTweets").show();
            }
            else {
                $("#noTweets").hide();
                $("#twitterContent").show();
            }
        },
        
        showMessage: function(message, level, dontFade){
            if (!message || message.trim() == "") {
                return;
            }
            if (!level) {
                level = "info";
            }
            var color = {
                "info": "GREEN",
                "error": "RED",
                "warn": "YELLOW"
            };
            $("#message").html(message);
            $("#message").css("backgroundColor", color[level]);
            $("#message").fadeIn();
            if (!dontFade) {
                window.setTimeout(function(){
                    $("#message").fadeOut();
                }, 5000);
            }
        },
    };
    
    TwitteyBot.init();
});

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
                        var num = parseInt(numbers[i - 1], 10) * u[unit];
                        if (!isNaN(num)) {
                            result += num;
                        }
                    }
                }
            }
            if (result == 0) {
                result = parseInt(interval, 10);
            }
            return isNaN(result) ? 0 : result;
        },
    
    });
    
    var TwitteyBot = {
        dateFormat: "dddd, MMMM dd, yyyy, hh:mm:ss tt",
        init: function(){
            var me = this;
            if ($("#twitterAccountList>li").size() == 0) {
                $("#twitterAccount").hide();
                $("#scheduler").hide();
                this.showMessage("Add a twitter account to tweet scheduled messages to it", "warn", true);
            }
            this.initTwitterAcccounts();
            this.initTwitterStatusActions();
            this.initScheduler();
            
            $(".actionBar>.right-Pane>div").hide();
            $("textarea,input[type=text]").bind("focus", function(){
                this.select();
            });
            $("#twitterAccountList a:first").click();
            window.setTimeout(this.updateVisibleTimes, 2000);
        },
        
        initTwitterAcccounts: function(){
            var me = this;
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
                        window.location = window.location.href;
                    }
                });
                return false;
            });
        },
        
        initTwitterStatusActions: function(){
            var me = this;
            $("#selectNoneStatus").click(function(){
                $("#twitterStatus .item-index").attr("checked", false);
                return false;
            });
            $("#selectAllStatus").click(function(){
                $("#twitterStatus .item-index").attr("checked", true);
                return false;
            });
            $("#twitterContent :reset").click(this.showTweets);
            
            $("#resultFrame").load(function(){
                $("#twitterStatus").html(this.contentDocument.body.innerHTML);
                me.onTweetsLoaded();
                me.showMessage($("#responseMessage").attr("value"), $("#responseMessage").attr("title"));
            });
            
            
            $("#twitterContent form").submit(function(){
                $("#uploadButtons").hide();
                $("#otherButtons").show();
            });
        },
        
        initScheduler: function(){
            var me = this;
            $("#scheduleStart a").click(function(){
                $("#scheduleStart *").toggle();
                $("#scheduleStart input[type=text]").focus();
                return false;
            });
            $("#scheduleStart input[type=text]").blur(function(){
                var startDate = Date.parse(this.value);
                if (startDate !== null) {
                    $("#scheduleStart *").toggle();
                    $("#scheduleStart a").html(startDate.toString(TwitteyBot.dateFormat));
                    $(this).css("border", "");
                }
                else {
                    $(this).css("border", "SOLID 1px RED");
                }
            });
            
            $("#scheduler table input[type=text]").blur(function(){
                var duration = $.parseInterval(this.value);
                if (this.duration == 0) {
                    $(this).css("border", "SOLID RED 1px");
                }
                else {
                    $(this).attr("value", duration + " minutes").css("border", "")
                }
            });
            
            $("#scheduler form").submit(function(){
                $("#scheduler input[type=text]").blur();
                var total = parseInt($("#totalItems").val(), 10) + 1;
                var interval = 0;
                if (nextTime === null) {
                    me.showMessage("Starting time is incorrect", "error");
                    return;
                }
                
                if ($("#scheduler input:checked").val() === "scheduleInterval") {
                    interval = parseInt($("#scheduleInterval").val());
                }
                else {
                    interval = parseInt($("#scheduleSpan").val(), 10) / (total);
                }
                
                var nextTime = Date.parse($("#scheduleStart input[type=text]").val());
                //nextTime.addMinutes(-1 * nextTime.getTimezoneOffset());
                
                for (var i = 0; i < total; i++) {
                    if ($("#item_" + i).val() === "on") {
                        $("#updatedTime_" + i).val(nextTime.getTime());
                        nextTime.addMinutes(interval);
                    }
                }
                me.updateVisibleTimes()
                return false;
            });
        },
        
        updateVisibleTimes: function(){
            $("#twitterStatus .actual-time").each(function(){
                var date = new Date();
                date.setTime($(this).val());
                //date.addMinutes(date.getTimezoneOffset());
                var identifier = $(this).attr("id").split("_")[1];
                $("#date_" + identifier).val(date.toString("dddd, MMMM dd, yyyy"));
                $("#time_" + identifier).val(date.toString("hh:mm:ss tt"));
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
				this.updateVisibleTimes();
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

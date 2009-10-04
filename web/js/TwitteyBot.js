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
    
    TwitteyBot.init();
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
            me.showTweets(true);
            return false;
        });
        
        $("#uploadFileForm form").submit(function(){
            me.showLoading();
            $(this).attr("action", "/pages/status?action=Upload&screenName=" + $("#twitterScreenName").html());
            $("#uploadButtons").show();
            $("#otherButtons").hide();
        });
        $("#deleteAccountForm form").submit(function(){
            me.showLoading();
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
            me.onTweetsLoaded(true);
            me.showMessage($("#responseMessage").attr("value"), $("#responseMessage").attr("title"));
        });
        
        $("#twitterContent form").submit(function(){
            me.showLoading();
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
            for (var i = 0; i < total; i++) {
                if ($("#item_" + i).attr("checked") === true) {
                    $("#updatedTime_" + i).val(nextTime.getTime());
                    nextTime.addMinutes(interval);
                }
            }
            me.updateVisibleTimes()
            return false;
        });
    },
    showLoading: function(){
        $("#showLoading").show();
        $("#twitterContent").hide();
        $("#noTweets").hide();
    },
    
    
    updateVisibleTimes: function(){
        var me = this;
        $("#twitterStatus .actual-time").each(function(){
            var date = new Date();
            date.setTime($(this).val());
            var identifier = $(this).attr("id").split("_")[1];
            $("#time_" + identifier).val(date.toString(TwitteyBot.dateFormat));
        });
        
        $(".tweetline textarea").each(function(){
            me.updateCharCount(this);
        });
    },
    
    showTweets: function(){
        var screenName = $("#twitterScreenName").html();
        var me = this;
        TwitteyBot.showLoading();
        $("#twitterStatus").load("/pages/status", {
            "action": "show",
            "screenName": screenName
        }, me.onTweetsLoaded);
        $("#uploadButtons").hide();
        $("#otherButtons").show();
    },
    
    onTweetsLoaded: function(caller){
        var me = TwitteyBot;
        $("#showLoading").hide();
        if ($("#twitterContent .tweetLine").size() === 0) {
            $("#twitterContent").hide();
            $("#noTweets").show();
        }
        else {
            $("#noTweets").hide();
            $("#twitterContent").show();
        }
        
        $(".tweetLine textarea").keypress(function(){
            me.updateCharCount(this);
        }).blur(function(){
            var identifier = $(this).attr("id").split("_")[1];
            $("#item_" + identifier).attr("checked", true);
            $(this).removeClass("focus-time");
        }).focus(function(){
            $(this).addClass("focus-time");
        })
        
        $(".tweetLine .time").blur(function(){
            var date = Date.parse($(this).val());
            if (date !== null) {
                $(this).val(date.toString(TwitteyBot.dateFormat));
                $(this).removeClass("focus-time");
                var identifier = $(this).attr("id").split("_")[1];
                $("#updatedTime_" + identifier).val(date.getTime());
                $("#item_" + identifier).attr("checked", true);
            }
            else {
                $(this).css("color", "red");
            }
        }).focus(function(){
            $(this).addClass("focus-time");
            $(this).select();
        });
        
        me.updateVisibleTimes();
    },
    
    updateCharCount: function(elem){
        var identifier = $(elem).attr("id").split("_")[1];
        var length = $(elem).val().length;
        if (length > 140) {
            $("#length_" + identifier).css("color", "#B93C0A");
        }
        else 
            if (length > 135) {
                $("#length_" + identifier).css("color", "#C1C23B");
            }
            else {
                $("#length_" + identifier).css("color", "#12B90A");
            }
        $("#length_" + identifier).html(length);
    },
    
    showMessage: function(message, level, dontFade){
        if (!message || message.trim() == "") {
            return;
        }
        if (!level) {
            level = "info";
        }
        var color = {
            "info": "#97F098",
            "error": "#F09797",
            "warn": "#EDF097"
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

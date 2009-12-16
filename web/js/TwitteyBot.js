$(document).ready(function(){
    $.extend({
        urlParser: function(url){
            var params = {};
            var separatorIndex = (url.lastIndexOf("?") >
            url.lastIndexOf("#")) ? url.lastIndexOf("?") : url.lastIndexOf("#");
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
                        var num = parseInt(numbers[i - 1], 10) *
                        u[unit];
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
        }
    });
    
    TwitteyBot.init();
});

var TwitteyBot = (function(){
    return {
        tweetLength: 140,
        dateFormat: "dddd, MMMM dd, yyyy, hh:mm:ss tt",
        selectAllByDefault: false,
        init: function(){
            var me = this;
            if ($("#twitterAccountList li").size() == 0) {
                $("#twitterAccount").hide();
                $("#scheduler").hide();
                $("#shrinker").hide();
                this.showMessage("<a href = '/pages/manageTwitterAccount?action=Add'>Manage</a> a twitter account and schedule tweets to it", "warn", true);
            }
            this.initTwitterAcccounts();
            this.initTwitterStatusActions();
            this.initScheduler();
            this.initShrinker();
            
            $(".actionBar>.right-pane>div").hide();
            $("textarea,input[type=text]").bind("focus", function(){
                this.select();
            });
            $("#twitterAccountList a:first").click();
        },
        
        initTwitterAcccounts: function(){
            var me = this;
            $("#actionList>li>a , #deleteAccountButton").click(function(event){
                $("#uploadFileForm").hide();
                $("#fetchFileForm").hide()
                $("#deleteAccountForm").hide();
                $("#actionList").hide();
                $(this.href.substring(this.href.indexOf("#"))).fadeIn();
                return false;
            });
            $(".actionBar :reset, :submit").click(function(){
                $("#uploadFileForm").hide();
                $("#fetchFileForm").hide();
                $("#deleteAccountForm").hide();
                $("#actionList").fadeIn();
            });
            $("#twitterAccountList a").click(function(event){
                var screenName = $.urlParser(this.href).params["screenName"];
                $("#twitterScreenName").html(screenName);
                $("#screenName").attr("value", screenName);
                me.showTweets(true);
                return false;
            });
            
            $("#sampleTweets").click(function(){
                $("#resultFrame").attr("src", "/pages/status?action=fetch&source_=http://twitteybot.appspot.com/tweets.txt&screenName=" +
                $("#twitterScreenName").html());
                me.showLoading();
                me.selectAllByDefault = true;
                $("#uploadButtons").show();
                $("#otherButtons").hide();
            });
            
            $("#fetchFileForm form").submit(function(){
                $(this).attr("action", "/pages/status?action=fetch&screenName=" +
                $("#twitterScreenName").html());
                me.showLoading();
                me.selectAllByDefault = true;
                $("#uploadButtons").show();
                $("#otherButtons").hide();
            });
            
            $("#uploadFileForm form").submit(function(){
            	var csvFlag = $("input#csvFile").attr("checked") ? "&csvFile=true" : "";
                $(this).attr("action", "/pages/status?action=Upload&screenName=" +
                $("#twitterScreenName").html() + csvFlag);
                me.selectAllByDefault = true;
                me.showLoading();
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
                        me.showMessage("Could not delete account " +
                        $("#twitterScreenName").html(), "error");
                    }
                    else {
                        me.showMessage($("#twitterScreenName").html() +
                        " deleted successfully. Refreshing accounts");
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
            
            this.onStatusLoad = function(content){
                $("#twitterStatus").html(content);
                me.onTweetsLoaded(true);
                me.showMessage($("#responseMessage").attr("value"), $("#responseMessage").attr("title"));
            };
            
            $("#twitterContent form").submit(function(){
                me.showLoading();
                $("#uploadButtons").hide();
                $("#otherButtons").show();
            });
        },
        
        initShrinker: function(){
            $("#shrinker-removeVowels").click(function(){
                ShrinkTweets("removeVowels");
            });
            $("#shrinker-wrapTweet").click(function(){
                ShrinkTweets("wrapTweet");
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
                    interval = parseInt($("#scheduleSpan").val(), 10) /
                    (total);
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
                $("#status_" + identifier).keypress();
                $("#time_" + identifier).val(date.toString(TwitteyBot.dateFormat));
            });
        },
        
        showTweets: function(arg, start, end){
            var screenName = $("#twitterScreenName").html();
            var me = this;
            TwitteyBot.showLoading();
            $("#twitterStatus").load("/pages/status", {
                "action": "show",
                "screenName": screenName,
                "start": start,
                "end": end
            });
            $("#uploadButtons").hide();
            $("#otherButtons").show();
        },
        
        onTweetsLoaded: function(caller){
            var me = TwitteyBot;
            var start = parseInt($("#page-start").val(), 10);
            var end = parseInt($("#page-end").val(), 10);
            
            var pageSize = 30;
            $("#showLoading").hide();
            if ($("#twitterContent .tweetLine").size() === 0) {
                if (start === 0) {
                    $("#twitterContent").hide();
                    $("#noTweets").show();
                }
                else {
                    me.showTweets(null, (start - pageSize) < 0 ? 0 : start - pageSize, start);
                }
                return;
            }
            else {
                $("#noTweets").hide();
                $("#twitterContent").show();
            }
            
            if (start <= 0) {
                $("div.pagination a.pagination-prev").hide();
            }
            else {
                $("div.pagination a.pagination-prev").show();
            }
            if (end - start < 30) {
                $("div.pagination a.pagination-next").hide();
            }
            else {
                $("div.pagination a.pagination-next").show();
            }
            
            
            $("div.pagination a.pagination-prev").click(function(e){
                me.showTweets(null, (start - pageSize) < 0 ? 0 : start - pageSize, start);
                return false;
            });
            
            $("div.pagination a.pagination-next").click(function(e){
                me.showTweets(null, end, end + pageSize);
                return false;
            });
            
            $("div.tweetLine textarea").keypress(function(){
                me.updateCharCount(this);
            }).blur(function(){
                var identifier = $(this).attr("id").split("_")[1];
                $("#item_" + identifier).attr("checked", true);
                $(this).removeClass("focus-time");
                me.updateCharCount(this);
            }).focus(function(){
                $(this).addClass("focus-time");
            })
            
            $("div.tweetLine .time").blur(function(){
                var date = Date.parse($(this).val());
                if (date !== null) {
                    $(this).css("color", "#3366CC");
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
            if (me.selectAllByDefault == true) {
                $(".item-index").attr("checked", true);
            }
            me.selectAllByDefault = false;
            me.updateVisibleTimes();
        },
        
        updateCharCount: function(elem){
            var identifier = $(elem).attr("id").split("_")[1];
            var length = $(elem).val().length;
            if (length > TwitteyBot.tweetLength) {
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
            if (!message || message == "") {
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
        
        addTweetLine: function(sourceField){
            var changeAttr = function(elem, attrib, num){
                $(elem).attr(attrib, $(elem).attr(attrib).split("_")[0] + "_" + num);
            }
            
            if (typeof(sourceField) === "undefined"){
				var result = $(".tweetLine:first").clone(true);	
			}else {
				var result = $(sourceField).clone(true);
			}
			
            var nextNum = parseInt($("#totalItems").val()) + 1;
            result.find("input, textarea").each(function(){
                changeAttr(this, "name", nextNum);
                changeAttr(this, "id", nextNum);
            });
            
            changeAttr(result.find(".screenName"), "id", nextNum);
            changeAttr(result.find(".source"), "id", nextNum);
            changeAttr(result.find(".length"), "id", nextNum);
            result.find("input[type=checkbox]").attr("checked", true);
            result.find(".tweet-key").val("");
            $("#totalItems").val(nextNum);
            return result;
        }
    }
})();

var ShrinkTweets = function(type){
    var tweetLength = TwitteyBot.tweetLength;
    var shrinkMethods = {
        "removeVowels": function(field){
            var vowelList = [/o/g, /u/g, /i/g, /a/g, /e/g];
            var tweetText = $(field).val();
            for (var i = 0; i < vowelList.length; i++) {
                if (tweetText.length > tweetLength) {
                    tweetText = tweetText.replace(vowelList[i], "");
                }
            }
            $(field).val(tweetText);
            return tweetText.length;
        },
        
        "wrapTweet": function(field){
            var tweetLine = $(field).parent().parent();
            var tweetText = $(field).val();
            var tweetFrags = [];
            while (tweetText.length > 0) {
                var frag = tweetText.substring(0, tweetLength);
                if (frag.lastIndexOf(" ") !== -1 && frag.length >= tweetLength) {
                    frag = frag.substring(0, frag.lastIndexOf(" "));
                }
                tweetFrags.push(frag);
                tweetText = tweetText.substring(frag.length);
            }
            
            $(field).val(tweetFrags[0]);
            for (var i = 1; i < tweetFrags.length; i++) {
                var newTweetLine = TwitteyBot.addTweetLine(tweetLine);
                tweetLine.after(newTweetLine);
                newTweetLine.find(".tweetText").val(tweetFrags[tweetFrags.length - i]);
                TwitteyBot.updateCharCount(newTweetLine.find(".tweetText"));
            }
        }
    };
    if (!type || typeof(shrinkMethods[type]) !== "function") {
        type = "removeVowels";
    }
    var me = this;
    $(".tweetText").each(function(){
        if ($(this).val().length > tweetLength) {
            var length = shrinkMethods[type].call(me, $(this));
            TwitteyBot.updateCharCount(this);
            $(this).parent().parent().find("input[type=checkbox]").attr("checked", true);
        }
    });
};

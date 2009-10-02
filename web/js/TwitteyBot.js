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
        }
    });
    
    var TwitteyBot = {
        init: function(){
            var me = this;
            if ($("#twitterAccountList>li").size() == 0) {
                $("#twitterAccount").hide();
                this.showMessage("Add a twitter account to tweet scheduled messages to it", "warn", true);
            }
            $(".actionBar>.right-Pane>div").hide();
            $("#actionList>li>a").click(function(event){
                $(".actionBar>.right-Pane>*").hide();
                $(this.href.substring(this.href.indexOf("#"))).fadeIn();
            });
            $(".actionBar :reset").click(function(){
                $(".actionBar>.right-Pane>*").hide();
                $(".actionBar>.right-Pane>ul").fadeIn();
            });
            $("#twitterAccountList a").click(this.onClickScreenName);
            $("#twitterAccountList a:first").click();
        },
        
        onClickScreenName: function(event){
            var screenName = $.urlParser(this.href).params["screenName"];
            $("#twitterScreenName").html(screenName);
            $("#twitterStatus").load("/pages/status", {
                "action": "show",
                "screenName": screenName
            }, this.onStatusLoaded);
            return false;
        },
        
        onStatusLoaded: function(){
        
        },
        
        showMessage: function(message, level, dontFade){
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
        
        onLoadStatus: function(){
        
        }
    };
    
    TwitteyBot.init();
});

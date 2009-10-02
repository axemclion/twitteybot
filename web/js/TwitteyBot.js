$(document).ready(function(){
    $.extend({
        urlHelper: function(url){
            var params = {};
            return {
                "params": params
            };
        }
    });
    
    var TwitteyBot = {
        init: function(){
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
            $("#twitterAccountList a").click(function(e){
            	
			});
        },
        
        showMessage: function(message, level, dontFade){
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
        
        onClickAccount: function(){
        
        },
        
        onLoadStatus: function(){
        
        }
    };
    
    TwitteyBot.init();
});

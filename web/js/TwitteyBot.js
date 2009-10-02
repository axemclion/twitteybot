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
            $(".actionBar>.right-Pane>div").hide();
            $("#actionList>li>a").click(function(event){
                $(".actionBar>.right-Pane>*").hide();
                $(this.href.substring(this.href.indexOf("#"))).fadeIn();
            });
            $(".actionBar :reset").click(function(){
                $(".actionBar>.right-Pane>*").hide();
                $(".actionBar>.right-Pane>ul").fadeIn();
            });
        },
        
        onClickAccount: function(){
        
        },
        
        onLoadStatus: function(){
        
        }
    };
    
    TwitteyBot.init();
});

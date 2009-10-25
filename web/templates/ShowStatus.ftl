<div id = "results">
<!--<#assign totalItems = "0"><#list statuses as item><#assign totalItems = item_index>-->
<div class = "tweetLine">
    <div class = "hidden-fields" style ="display:none">
        <input type = "text" class = "actual-time" id = "updatedTime_${item_index}" name = "updatedTime_${item_index}" value = "${item.time}"/>
        <input type = "text" class = "tweet-key" id = "key_${item_index}" name = "key_${item_index}" value = "${item.keyId!""}"/>
        <input type = "text" class = "tweet-source" id = "source_${item_index}" name = "source_${item_index}" value = "${item.source}"/>
    </div>
    <div class = "left-pane" style ="width:2%">
        <input type = "checkbox" id = "item_${item_index}" name = "item_${item_index}" class = "item-index" value = "on"/>
    </div>
    <div class = "right-pane" style ="width:97%">
        <textarea id = "status_${item_index}" name = "status_${item_index}" class = "multiline-text tweetText">${item.status}</textarea>
    </div>
    <div class = "clear bottom-line">
        <div class = "left-pane single-line-list" style = "right; padding : 0.5em;">
            <span class = "screenName" id = "screenName_${item_index}">
                ${item.twitterScreenName}
            </span>
            <span class = "source" id = "source_${item_index}">
                <span style ="color : #000000">| </span>
                ${item.source} <span style ="color : #000000">| </span>
            </span>
            <span>
                <input title = "click to edit" class = "time" id = "time_${item_index}">
                </input>
            </span>
        </div>
        <div class = "right-pane" style ="width : 30%; text-align : right; padding : 0.5em; color:#3366CC">
            <span id = "length_${item_index}" class = "length"></span>
            characters
        </div>
        <div class = "clear">
        </div>
    </div>
</div>
<!--</#list>-->
<br/>
<input type = "hidden" name = "totalItems" id = "totalItems" value = "${totalItems}" />
<input id = "responseMessage" title = "${level!""}" style = "display:none" value ='${message!""}'/>
</div>
<script>
	top.onIFrameLoad(document.getElementById("results").innerHTML);
</script>
<!--<#assign totalItems = "0"><#list statuses as item><#assign totalItems = item_index>-->
<div class = "tweetLine">
    <div class = "hidden-fields" style ="display:none">
        <input type = "text" class = "actual-time" id = "updatedTime_${item_index}" name = "updatedTime_${item_index}" value = "${item.time}"/>
        <input type = "text" name = "key_${item_index}" value = "${item.keyId!""}"/>
        <input type = "text" name = "source_${item_index}" value = "${item.source}"/>
    </div>
    <div class = "left-pane" style ="width:2%">
        <input type = "checkbox" id = "item_${item_index}" name = "item_${item_index}" class = "item-index" value = "on"/>
    </div>
    <div class = "right-pane" style ="width:97%">
        <textarea id = "status_${item_index}" name = "status_${item_index}" class = "multiline-text">${item.status}</textarea>
    </div>
    <div class = "clear bottom-line">
        <ul class = "left-pane single-line-list">
            <li class = "screenName" id = "screenName_${item_index}">
                ${item.twitterScreenName}
            </li>
            <li class = "source" id = "source_${item_index}">
                <span style ="color : #000000">| </span>
                ${item.source} <span style ="color : #000000">| </span>
            </li>
            <li>
                <input title = "click to edit" class = "time" id = "time_${item_index}">
                </input>
            </li>
        </ul>
        <div class = "right-pane length" style ="width : 30%; text-align : right; padding : 0.5em; color:#3366CC">
            <span id = "length_${item_index}"></span>
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

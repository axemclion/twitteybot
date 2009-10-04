<table cellpadding = "0" cellspacing = 0>
    <tr>
        <th>
            &nbsp;
        </th>
        <th>
            Time
        </th>
        <th>
            Tweet
        </th>
    </tr>
    <#assign totalItems = "0"><#list statuses as item><#assign totalItems = item_index>
    <tr class = "tweetLine">
        <td>
            <input type = "checkbox" id = "item_${item_index}" name = "item_${item_index}" checked = "true" class = "item-index" value = "on"/><input type = "hidden" name = "key_${item_index}" value = "${item.keyId!""}"/><input type = "hidden" name = "source_${item_index}" value = "${item.source}"/>
        </td>
        <td>
            <input type = "text" class = "update-date" id = "date_${item_index}" value = "${item.updatedTime?string("EEEE, MMMM dd, yyyy")}"/>
            <br/>
            <input type = "text" class = "update-time" id = "time_${item_index}" value = "${item.updatedTime?string("hh:mm:ss a")}"/>
            <br/>
            <input type = "text" class = "actual-time" id = "updatedTime_${item_index}" name = "updatedTime_${item_index}" class = "text-box" value = "${item.time}"/>
        </td>
        <td>
            <textarea id = "status_${item_index}" name = "status_${item_index}" class = "multiline-text">${item.status}</textarea>
        </td>
    </tr>
    </#list>
</table>
<input type = "hidden" name = "totalItems" id = "totalItems" value = "${totalItems}" /><input id = "responseMessage" title = "${level!""}" style = "display:none" value ='${message!""}'/>
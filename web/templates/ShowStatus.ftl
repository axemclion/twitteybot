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
    <tr>
        <td>
            <input type = "checkbox" name = "item_${item_index}" checked = "true" class = "item-index" value = "on"/><input type = "hidden" name = "key_${item_index}" value = "${item.keyId!""}"/><input type = "hidden" name = "source_${item_index}" value = "${item.source}"/>
        </td>
        <td>
            <input type = "text" name = "updatedTime_${item_index}" class = "text-box" value = "${item.updatedTime?string("EEEE,  MMMM dd,  yyyy,  hh:mm:ss a '('zzz')'")}" />
        </td>
        <td>
            <textarea name = "status_${item_index}" class = "multiline-text">
                ${item.status}
            </textarea>
        </td>
    </tr>
    </#list>
</table>
<input type = "hidden" name = "totalItems" value = "${totalItems}" /><span id = "responseMessage" title = "${level!""}" style = "display:none">${message!""}  </span>

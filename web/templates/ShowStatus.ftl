<table cellpadding = "0" cellspacing = 0>
    <tr>
        <th></th>
        <th>Updated Time</th>
        <th>Updated Time</th>
        <th>Updated Time</th>
    </tr>
    <#assign totalItems = "0">
    <#list statuses as item>
    <#assign totalItems = item_index>
    <tr>
        <td>
        	<input type = "checkbox" name = "item_${item_index}" checked = "true" class = "item-index"/>
        </td>
        <td>
        	<input type = "text" name = "twitterScreenName_${item_index}" value = "${item.twitterScreenName}" />
        </td>
        <td>
        	<input type = "text" name = "source_${item_index}" value = "${item.source}" />
        </td>
        <td>
            <textarea name = "status_${item_index}" >${item.status}</textarea>
        </td>
        <td>
        	<input type = "text" name = "updatedTime_${item_index}" value = "${item.updatedTime?string("EEEE, MMMM dd, yyyy, hh:mm:ss a '('zzz')'")}" />  
        </td>
        <td>
        	<input type = "checkbox" name = "canDelete_${item_index}" checked = "true" />
        </td>
    </tr>
    </#list>
</table>
<input type = "hidden" name = "totalItems" value = "${totalItems}" />
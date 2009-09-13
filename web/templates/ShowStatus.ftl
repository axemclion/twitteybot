<div>
	<form method = "POST" action = "/pages/status">
		<input type = "submit"/>
		<input name = "action" id = "action" value = "${action}" />
	    <table border = "1">
	        <tr>
	            <th>Status Message</th>
	            <th>Updated Time</th>
	            <th>Updated Time</th>
	            <th>Updated Time</th>
	        </tr>
	        <#assign totalItems = "0">
	        <#list statuses as item>
	        <#assign totalItems = item_index>
	        <tr>
	            <td>
	            	<input type = "checkbox" name = "item_${item_index}" checked = "true" />
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
	            	<input type = "text" name = "updatedTime_${item_index}" value = "${item.updatedTime?datetime}" />  
	            </td>
	        </tr>
	        </#list>
	    </table>
	    <input name = "totalItems" value = "${totalItems}" />
        <input type = "submit"/>
    </form>
</div>

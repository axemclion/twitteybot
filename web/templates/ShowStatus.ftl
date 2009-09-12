<div>
    <table border = "1">
        <tr>
            <th>Status Message</th>
            <th>Updated Time</th>
        </tr>
        <#list statuses as item>
        <tr>
            <td>
            	${item.twitterScreenName}
            </td>
            <td>
            	${item.source}
            </td>

            <td>
                <textarea disabled = "true">${item.status}</textarea>
            </td>
            
        </tr>
        </#list>
    </table>
</div>

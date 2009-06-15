<div>
    <table border = "1">
        <tr>
            <th>Status Message</th>
            <th>Updated Time</th>
        </tr>
        <#list status as item>
        <tr>
            <td>
                <textarea disabled = "true">${item.status}</textarea>
            </td>
            <td>${item.updatedTime?datetime}</td>
        </tr>
        </#list>
    </table>
</div>

<div>
    <ul>
        <#list feeds as item>
        <li>
            <a href = "${statusPage}&action=show&feedname=${item}" target = "_self">${item}</a>
            <a href = "${feedsPage}&action=edit&feedname=${item}" target="_blank">[edit]</a>
            <a href="${feedsPage}&action=delete&feedname=${item}" target="_blank">[delete]</a>
        </li>
        </#list>
    </ul>
    <div id = "addFeedAccount">
        <a href = "${feedsPage}" target="_blank">Add a new feed</a>
    </div>
    <div id = "fileUploader">
        <form action="${uploadPage}&twitter=${twitter}" method="post" enctype='multipart/form-data'>
            <input type = "text" name = "twitter" value = "${twitter}"/><input type = "file" name = "statusFile"/><input type = "submit"/>
        </form>
    </div>
</div>
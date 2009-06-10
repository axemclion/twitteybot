<div>
    <ul>
        <#list feeds as item>
        <li>
            ${item}<a href = "${feedsPage}&action=edit&feedname=${item}" target="_blank">[edit]</a>
            <a href="${feedsPage}&action=delete&feedname=${item}" target="_blank">[delete]</a>
        </li>
        </#list>
    </ul>
    <div id = "addFeedAccount">
        <a href = "${feedsPage}" target="_blank">Add a new feed</a>
    </div>
</div>

<div class = "twitter-accounts">
    <ul>
        <#list accounts as item>
        <li>
            <a href = "/pages/feeds?action=show"> ${item}</a>
            <a href = "${twitterPage}action=edit&username=${item}" target="_blank">[edit]</a>
            <a href="${twitterPage}action=delete&username=${item}" target="_blank">[delete]</a>
        </li>
        </#list>
    </ul>
</div>
<div id = "addTwitterAccount">
    <a href = "${twitterPage}" target="_blank">Add a new account</a>
</div>

<div>
    <form action = "${targetUrl}" method = "POST">
        <label for = "${feedname}">Feed Name</label>
        <input name = "${feedname}" type = "text" value = "${feedNameValue!""}" />
        <label for = "${interval}">Interval</label>
        <input name = "${interval}" type = "text" value = "${intervalValue!"10"}"/>
        <br/>
        <input type = "SUBMIT"/>
    </form>
</div>
<div>
    <form action = "${targetUrl}" method = "POST">
        <label for = "${username}">Username</label>
        <input name = "${username}" type = "text" value = "${usernameValue!""}" />
        <label for = "${password}">Password</label>
        <input name = "${password}" type = "password"/>
        <label for = "${interval}">Interval</label>
        <input name = "${interval}" type = "text" value = "${intervalValue!"10"}"/>
        <br/>
        <input type = "SUBMIT"/>
    </form>
</div>
<html>
    <head>
        <title>Time2Tweet :: Schedule your tweets</title>
        <link rel="stylesheet" type=text/css href="/css/main.css"/>
    </head>
    <body style ="margin:auto">
        <h1>Configure the application</h1>
        <form method = "POST" action="/admin?action=add">
            <table style ="width : 80%; align : center; border : SOLID 1px BLACK">
                <tr>
                    <td colspan=2>
                        Twitter Configuration
                    </td>
                </tr>
                <tr>
                    <td>
                        Consumer Key
                    </td>
                    <td>
                        <input name = "consumer_key" value = '${consumer_key!""}'>
                    </td>
                </tr>
                <tr>
                    <td>
                        Consumer Secret
                    </td>
                    <td>
                        <input name = "consumer_secret" value = '${consumer_secret!""}'>
                    </td>
                </tr>
				<tr>
					<td colspan = "2">
						<input type = "submit" value = "Save">
					</td>
				</tr>
            </table>
        </form>
    </body>
</html>
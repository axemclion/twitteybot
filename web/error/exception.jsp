<%@page isErrorPage="true" %>
<html>
    <head>
        <title>Internal Server Error</title>
    </head>
    <body>
        <center>
            <H1>Oops, looks like we hit an internal server error</H1>
            <br/>
            <h2>Mistakes happen, but the good thing is that you cna let us know so that we can correct it
                <br/>
                Please send <a id = "mailTo">us</a> details about what happened and we will look into it. 
            </h2>
        </center>
        <code style ="display : none; text-align:left" id = "exception">
            <%exception.printStackTrace(new java.io.PrintWriter(out)); %>
        </code>
    </body>
    <script type="text/javascript">
    	document.getElementById("mailTo").href = ["mailTo:sneakoscope@gmail.com?","subject=Exception", "&body=", document.getElementById("exception").innerHTML].join("");
        if (top.location != window.location) {
            top.location = window.location;
        }
    </script>
</html>

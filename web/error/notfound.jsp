<%@page isErrorPage="true" %>
<html>
    <head>
        <title>Page Not Found</title>
    </head>
    <body>
        <center>
            <H1>The page you tried to access does not exist</H1>
            <br/>
            <h2>
            You are being redirected to the <A href= "/index.html">home page</A>. 
            </a>
        </h2>
        </center>
    </body>
    <script type = "text/javascript">
        window.setTimeout(redirect, 10000);
        function redirect(){
            if (top.location != window.location) {
                top.location = "/index.html";
            }
            else {
                window.location = "/index.html";
            }
        }
    </script>
</html>

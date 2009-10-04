<%@page isErrorPage="true" %>
<html>
    <head>
        <title>Internal Server Error</title>
    </head>
    <body>
        <H1>Oops, looks like we hit an internal server error</H1>
        <br/>
        <h2>Mistakes happen, but the good thing is that you cna let us know so that we can correct it
            <br/>
            Please send <a href = "mailto:sneaoscope.application@gmail.com">us</a> details about what happened and we will look into it. 
        </h2>
        <div style ="display : hidden">
        	<% exception.printStackTrace(out)%>
        </div>
    </body>
    <script type="text/javascript">
        if (top.location != window.location) {
            top.location = window.location;
        }
    </script>
</html>

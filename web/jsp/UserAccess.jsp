<%@ page import = "com.google.appengine.api.users.*" %>
<%@ page import = "com.appspot.twitteybot.ui.*" %>

<%
UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();

if (userService.getCurrentUser() == null) {
    response.sendRedirect(Pages.LANDING_PAGE + userService.createLoginURL(req.getRequestURI()));
%>
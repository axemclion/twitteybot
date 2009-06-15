package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.helper.UserConfigDataHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MainPage extends HttpServlet {

    private static final long serialVersionUID = 9148447220528278458L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	Map<String, Object> templateValues = new HashMap<String, Object>();

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if (user == null) {
	    resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
	} else {
	    templateValues.put(Pages.FTLVAR_USERNAME, user.getNickname());
	}

	UserConfigDataHelper userConfigHelper = new UserConfigDataHelper(user);
	if (userConfigHelper.getUserConfig() == null && user != null) {
	    // TODO Allow only admin to create users
	    userConfigHelper.createNewUser(user);
	}

	FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_MAIN_PAGE, resp.getWriter());
    }
}

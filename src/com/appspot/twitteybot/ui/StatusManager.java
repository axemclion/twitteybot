package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.helper.StatusHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Manages the status messages
 */
public class StatusManager extends HttpServlet {
    private static final long serialVersionUID = 1888334549313689006L;

    // private static final String PARAM_ACTION = "action";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a registered user to perform this operation");
	    return;
	}

	String feedName = req.getParameter(Pages.PARAM_FEED_NAME);
	String twitterName = req.getParameter(Pages.PARAM_TWITTER);

	StatusHelper helper = new StatusHelper(user, twitterName, feedName);
	Map<String, Object> props = new HashMap<String, Object>();
	props.put(Pages.FTLVAR_STATUS, helper.getAllStatus());
	FreeMarkerConfiguration.writeResponse(props, Pages.TEMPLATE_SHOW_STATUS, resp.getWriter());

    }
}

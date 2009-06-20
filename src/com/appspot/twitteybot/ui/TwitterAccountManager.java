package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.helper.TwitterDataHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Manages adding and deletion of Twitter accounts to the database
 */
public class TwitterAccountManager extends HttpServlet {
    private static final long serialVersionUID = 929636040366447125L;

    private static final Logger log = Logger.getLogger(TwitterAccountManager.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a registered user to perform this operation");
	    return;
	}

	String action = req.getParameter(Pages.PARAM_ACTION);
	TwitterDataHelper twitterHelper = new TwitterDataHelper(user);

	log.log(Level.FINE, "Action ", action);

	if (action == null) {
	    action = Pages.ACTION_EDIT;
	}

	if (action.equals(Pages.ACTION_ADD)) {
	    String username = req.getParameter(Pages.PARAM_USER_NAME);
	    String password = req.getParameter(Pages.PARAM_PASSWORD);
	    int interval = Integer.parseInt(req.getParameter(Pages.PARAM_INTERVAL));
	    if (!twitterHelper.setTwitterAccount(username, password, interval)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or update twitter account");
		return;
	    }
	} else if (action.equals(Pages.ACTION_DELETE)) {
	    String username = req.getParameter(Pages.PARAM_USER_NAME);
	    if (!twitterHelper.deleteAccount(username)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not delete twitter account");
		return;
	    }
	} else if (action.equals(Pages.ACTION_SHOW)) {
	    Map<String, Object> props = new HashMap<String, Object>();
	    List<String> twitterAccounts = new ArrayList<String>();
	    
	    for (TwitterAccount twitterAccount : twitterHelper.getAllTwitterAccounts()) {
		twitterAccounts.add(twitterAccount.getTwitterName());
	    }

	    props.put(Pages.FTLVAR_TWITTER_ACCOUNTS, twitterAccounts);
	    props.put(Pages.FTLVAR_PAGE_TWITTER, Pages.PAGE_TWITTER_ACCOUNTS);
	    props.put(Pages.FTLVAR_PAGE_FEEDS, Pages.PAGE_FEEDS);

	    FreeMarkerConfiguration.writeResponse(props, Pages.TEMPlATE_SHOW_ACCOUNT, resp.getWriter());

	} else {
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(Pages.PARAM_USER_NAME, Pages.PARAM_USER_NAME);
	    props.put(Pages.PARAM_PASSWORD, Pages.PARAM_PASSWORD);
	    props.put(Pages.PARAM_INTERVAL, Pages.PARAM_INTERVAL);
	    TwitterAccount account = twitterHelper.getTwitterAccount(req.getParameter(Pages.PARAM_USER_NAME));
	    if (account != null) {
		props.put(Pages.FTLVAR_USERNAME_VALUE, account.getTwitterName());
		props.put(Pages.FTLVAR_INTERVAL_VALUE, account.getTwitterInterval());
	    }
	    props.put(Pages.FTLVAR_TARGET_URL, Pages.PAGE_TWITTER_ACCOUNTS + Pages.PARAM_ACTION + "="
		    + Pages.ACTION_ADD);
	    FreeMarkerConfiguration.writeResponse(props, Pages.TEMPLATE_ADD_ACCOUNT, resp.getWriter());
	}
	twitterHelper.commit();
    }
}

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

    static final String PARAM_ACTION = "action";
    static final String ACTION_ADD_ACCOUNT = "add";
    static final String ACTION_DELETE_ACCOUNT = "delete";
    static final String ACTION_EDIT_ACCOUNT = "edit";
    static String ACTION_SHOW_ACCOUNTS = "show";

    static final String PARAM_USER_NAME = "username";
    static final String PARAM_PASSWORD = "password";
    static final String PARAM_INTERVAL = "interval";

    private static final String FTL_TWITTER_ACCOUNTS = "accounts";
    private static final String FTL_PAGE_TWITTER = "twitterPage";
    private static final String FTL_PAGE_FEEDS = "feedsPage";

    private static final String ADD_ACCOUNT_FTL = "AddTwitterAccount.ftl";
    private static final String FTL_SHOW_ACCOUNT = "ShowAccounts.ftl";

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

	String action = req.getParameter(PARAM_ACTION);
	TwitterDataHelper twitterHelper = new TwitterDataHelper(user);

	log.log(Level.FINE, "Action ", action);

	if (action == null) {
	    action = ACTION_EDIT_ACCOUNT;
	}

	if (action.equals(ACTION_ADD_ACCOUNT)) {
	    String username = req.getParameter(PARAM_USER_NAME);
	    String password = req.getParameter(PARAM_PASSWORD);
	    Long interval = Long.parseLong(req.getParameter(PARAM_INTERVAL));
	    if (!twitterHelper.setTwitterAccount(username, password, interval)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or update twitter account");
		return;
	    }
	} else if (action.equals(ACTION_DELETE_ACCOUNT)) {
	    String username = req.getParameter(PARAM_USER_NAME);
	    if (!twitterHelper.deleteAccount(username)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not delete twitter account");
		return;
	    }
	} else if (action.equals(ACTION_SHOW_ACCOUNTS)) {
	    Map<String, Object> props = new HashMap<String, Object>();
	    List<String> twitterAccounts = new ArrayList<String>();
	    for (TwitterAccount twitterAccount : twitterHelper.getAllTwitterAccounts()) {
		twitterAccounts.add(twitterAccount.getUserName());
	    }

	    props.put(FTL_TWITTER_ACCOUNTS, twitterAccounts);
	    props.put(FTL_PAGE_TWITTER, Pages.PAGE_TWITTER_ACCOUNTS);
	    props.put(FTL_PAGE_FEEDS, Pages.PAGE_FEEDS);

	    FreeMarkerConfiguration.writeResponse(props, FTL_SHOW_ACCOUNT, resp.getWriter());

	} else {
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(PARAM_USER_NAME, PARAM_USER_NAME);
	    props.put(PARAM_PASSWORD, PARAM_PASSWORD);
	    props.put(PARAM_INTERVAL, PARAM_INTERVAL);
	    TwitterAccount account = twitterHelper.getTwitterAccount(req.getParameter(PARAM_USER_NAME));
	    if (account != null) {
		props.put("usernameValue", account.getUserName());
		props.put("intervalValue", account.getTwitterInterval());
	    }
	    props.put("targetUrl", Pages.PAGE_TWITTER_ACCOUNTS + PARAM_ACTION + "=" + ACTION_ADD_ACCOUNT);
	    FreeMarkerConfiguration.writeResponse(props, ADD_ACCOUNT_FTL, resp.getWriter());
	}
	twitterHelper.commit();
    }
}

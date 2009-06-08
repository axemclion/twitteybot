package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.DataStoreHelper;
import com.appspot.twitteybot.datastore.TwitterAccount;
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
    static final Object ACTION_ADD_ACCOUNT = "add";
    static final Object ACTION_DELETE_ACCOUNT = "delete";
    static final String ACTION_EDIT_ACCOUNT = "edit";

    static final String PARAM_USER_NAME = "username";
    static final String PARAM_PASSWORD = "password";
    static final String PARAM_INTERVAL = "interval";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	String action = req.getParameter(PARAM_ACTION);
	if (action == null) {
	    action = "addNewAccount";
	}
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	DataStoreHelper storeHelper = new DataStoreHelper(user);
	TwitterAccount account = storeHelper.getTwitterAccount(req.getParameter(PARAM_USER_NAME));
	log.log(Level.FINE, "Action ", action);

	if (action.equals(ACTION_ADD_ACCOUNT)) {
	    if (storeHelper.getTwitterAccount(req.getParameter(PARAM_USER_NAME)) != null) {
		resp.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Twitter name already exists");
	    } else {
		if (account == null) {
		    account = new TwitterAccount();
		    account.setUserName(req.getParameter(PARAM_USER_NAME));
		    account.setPassword(req.getParameter(PARAM_PASSWORD));
		    account.setTwitterInterval(Long.parseLong(req.getParameter(PARAM_INTERVAL)));
		    storeHelper.getUserConfig().getTwitterAccounts().add(account);
		    log.log(Level.INFO, "Account not found, so creating one", account.getUserName());
		} else {
		    account.setPassword(req.getParameter(PARAM_PASSWORD));
		    account.setTwitterInterval(Long.parseLong(req.getParameter(PARAM_INTERVAL)));
		    log.log(Level.INFO, "Account found, so updating it", account.getUserName());
		}
		storeHelper.commit();
		resp.setStatus(HttpServletResponse.SC_OK);
	    }
	} else if (action.equals(ACTION_EDIT_ACCOUNT)) {
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(PARAM_USER_NAME, PARAM_USER_NAME);
	    props.put(PARAM_PASSWORD, PARAM_PASSWORD);
	    props.put(PARAM_INTERVAL, PARAM_INTERVAL);

	    props.put("usernameValue", account.getUserName());
	    props.put("intervalValue", account.getTwitterInterval());

	    props.put("targetUrl", Pages.PAGE_TWITTER_ACCOUNTS + PARAM_ACTION + "=" + ACTION_EDIT_ACCOUNT);
	    FreeMarkerConfiguration.writeResponse(props, Pages.FTL_ADD_ACCOUNT, resp.getWriter());
	} else if (action.equals(ACTION_DELETE_ACCOUNT)) {
	    if (account == null) {
		resp.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Twitter name does not exist");
	    } else {
		resp.setStatus(HttpServletResponse.SC_OK);
	    }
	} else {
	    Map<String, Object> props = new HashMap<String, Object>();
	    // This is kinda strange, but the idea is to have the named params
	    // of the forms controlled here
	    props.put(PARAM_USER_NAME, PARAM_USER_NAME);
	    props.put(PARAM_PASSWORD, PARAM_PASSWORD);
	    props.put(PARAM_INTERVAL, PARAM_INTERVAL);
	    props.put("targetUrl", Pages.PAGE_TWITTER_ACCOUNTS + PARAM_ACTION + "=" + ACTION_ADD_ACCOUNT);
	    FreeMarkerConfiguration.writeResponse(props, Pages.FTL_ADD_ACCOUNT, resp.getWriter());
	}
    }
}

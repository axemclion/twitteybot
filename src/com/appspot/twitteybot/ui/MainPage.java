package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.DataStoreHelper;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MainPage extends HttpServlet {

    private static final long serialVersionUID = 9148447220528278458L;
    private static final String FTL_TWITTER_ACCOUNTS = "accounts";
    private static final String FTL_USERNAME = "username";
    private static final String FTL_TWITTERACCOUNT = "twitterPage";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	Map<String, Object> templateValues = new HashMap<String, Object>();

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if (user == null) {
	    resp.sendRedirect(Pages.LANDING_PAGE + userService.createLoginURL(req.getRequestURI()));
	} else {
	    templateValues.put(FTL_USERNAME, user.getNickname());
	}

	DataStoreHelper storeHelper = new DataStoreHelper(user);

	List<String> twitterAccounts = new ArrayList<String>();
	for (TwitterAccount account : storeHelper.getAllTwitterAccounts()) {
	    twitterAccounts.add(account.getUserName());
	}

	templateValues.put(FTL_TWITTER_ACCOUNTS, twitterAccounts);
	templateValues.put(FTL_TWITTERACCOUNT, Pages.PAGE_TWITTER_ACCOUNTS);
	FreeMarkerConfiguration.writeResponse(templateValues, Pages.FTL_MAIN_PAGE, resp.getWriter());
    }
}

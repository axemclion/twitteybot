package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.PMF;
import com.appspot.twitteybot.datastore.Settings;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import freemarker.template.TemplateException;

public class MainPage extends HttpServlet {

    private static final long serialVersionUID = 9148447220528278458L;
    private static final String FTL_TWITTER_ACCOUNTS = "accounts";
    private static final String FTL_USERNAME = "username";
    private static final String FTL_LOGIN_URL = "loginUrl";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	Map<String, Object> templateValues = new HashMap<String, Object>();

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if (userService.getCurrentUser() == null) {
	    resp.sendRedirect(Pages.LANDING_PAGE + userService.createLoginURL(req.getRequestURI()));
	} else {
	    templateValues.put(FTL_USERNAME, user.getNickname());
	}

	templateValues.put(FTL_TWITTER_ACCOUNTS, this.getTwitterAccountsForUser(user));
	FreeMarkerConfiguration.writeResponse(templateValues, "userMain.ftl", resp.getWriter());
    }

    private List<String> getTwitterAccountsForUser(User user) {
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Query query = pm.newQuery(Settings.class);
	query.setFilter("user == userName");
	query.declareParameters("com.google.appengine.api.users.User userName");
	List<Settings> results = (List<Settings>) query.execute(user);
	List<String> twitter = new ArrayList<String>();
	for (Settings result : results) {
	    for (TwitterAccount account : result.getTwitterAccounts()) {
		twitter.add(account.getUserName());
	    }
	}
	return twitter;
    }
}

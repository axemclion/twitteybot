package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class MainPage extends HttpServlet {

	private static final long serialVersionUID = 9148447220528278458L;
	private static final Logger log = Logger.getLogger(StatusFileUploader.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {

		User user = UserServiceFactory.getUserService().getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		log.info("Working for user " + user.getEmail());
		Query query = pm.newQuery(TwitterAccount.class);
		query.setFilter("user == userVar");
		query.declareParameters("com.google.appengine.api.users.User userVar");
		@SuppressWarnings("unchecked")
		List<TwitterAccount> twitterAccounts = (List<TwitterAccount>) query.execute(user);

		if (twitterAccounts == null || twitterAccounts.size() == 0) {
			twitterAccounts = new ArrayList<TwitterAccount>();
		}

		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_USERNAME, user.getEmail());
		templateValues.put(Pages.FTLVAR_LOGOUT, UserServiceFactory.getUserService().createLogoutURL(
				Pages.PAGE_HOME));
		templateValues.put(Pages.FTLVAR_TWITTER_ACCOUNTS, twitterAccounts);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_MAIN_PAGE, resp.getWriter());
	}
}

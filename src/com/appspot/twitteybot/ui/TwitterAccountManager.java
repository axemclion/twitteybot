package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.google.appengine.api.users.UserServiceFactory;

public class TwitterAccountManager extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String consumerKey = "LJ2GIzMFLCuuXtjLUvXlJA";
	public static final String consumerSecret = "JjFecOMdVXLClJo8TNy5J9KXGKpFQbk0eXHRw3PZC8c";
	private static final String COOKIE_TOKEN = "token";
	private static final String COOKIE_TOKEN_SECRET = "token_secret";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);

		if (action == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		try {
			if (action.equals(Pages.PARAM_ACTION_ADD)) {
				RequestToken requestToken = twitter.getOAuthRequestToken();
				resp.addCookie(new Cookie(COOKIE_TOKEN, requestToken.getToken()));
				resp.addCookie(new Cookie(COOKIE_TOKEN_SECRET, requestToken.getTokenSecret()));
				resp.sendRedirect(requestToken.getAuthorizationURL());
			} else if (action.equals(Pages.PARAM_OAUTH)) {
				String token = null, tokenSecret = null;
				Cookie[] cookies = req.getCookies();
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(COOKIE_TOKEN)) {
						token = cookie.getValue();
					}
					if (cookie.getName().equals(COOKIE_TOKEN_SECRET)) {
						tokenSecret = cookie.getValue();
					}
				}
				AccessToken accessToken = twitter.getOAuthAccessToken(token, tokenSecret);
				this.saveToken(accessToken);
				resp.getWriter().write("<script>window.close();</script>");
			} else if (action.equals(Pages.PARAM_ACTION_DELETE)) {
				this.deleteToken(req.getParameter(Pages.PARAM_SCREENNAME));
				resp.getWriter().write("Delete Successful");
			}
		} catch (TwitterException e) {
			e.printStackTrace(resp.getWriter());
		}
	}

	private void saveToken(AccessToken token) {
		TwitterAccount twitterAccount = new TwitterAccount();
		twitterAccount.setUser(UserServiceFactory.getUserService().getCurrentUser());
		twitterAccount.setToken(token.getToken());
		twitterAccount.setSecret(token.getTokenSecret());
		twitterAccount.setTwitterScreenName(token.getScreenName());
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(twitterAccount);
		pm.close();
	}

	private void deleteToken(String screenName) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(TwitterAccount.class);
		query.setFilter("twitterScreenName == screenVar");
		query.declareParameters("String screenVar");
		@SuppressWarnings("unchecked")
		List<TwitterAccount> twitterAccounts = (List<TwitterAccount>) query.execute(screenName);
		pm.deletePersistentAll(twitterAccounts);
		query.closeAll();
		pm.close();
	}
}

package com.appspot.twitteybot;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.ui.Pages;

public class TaskServlet extends HttpServlet {
	private static final String TWITTER_ACCOUNT_CACHE = "twitterAccountCache";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action == null) {
			return;
		} else if (action.equals(Pages.PARAM_ACTION_UPDATE)) {
			String twitterScreenName = req.getParameter(Pages.PARAM_STATUS_TWITTER_SCREEN);
			TwitterAccount twitterAccount = this.getTwitterAccount(req
					.getParameter(Pages.PARAM_STATUS_STATUS));
			if (twitterScreenName == null || twitterAccount == null) {
				throw new ServletException("Error with input parameters");
			}
			Twitter twitter = new Twitter();
			twitter.setOAuthAccessToken(twitterAccount.getToken(), twitterAccount.getSecret());
			twitter.setSource("http://twitteybot.appspot.com");
			try {
				twitter.updateStatus(req.getParameter(Pages.PARAM_STATUS_STATUS));
			} catch (TwitterException e) {
				throw new ServletException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private TwitterAccount getTwitterAccount(String screenName) throws ServletException {
		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(TWITTER_ACCOUNT_CACHE);
		if (cache == null) {
			try {
				cache = cacheManager.getCacheFactory().createCache(Collections.emptyMap());
			} catch (CacheException e) {
				throw new ServletException(e);
			}
			cacheManager.registerCache(TWITTER_ACCOUNT_CACHE, cache);
		}
		Object obj = cache.get(screenName);
		TwitterAccount result = null;
		if (obj instanceof TwitterAccount) {
			result = (TwitterAccount) obj;
		}
		if (result == null) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery();
			query.setFilter("twitterScreenName twitterScreenNameVar");
			query.declareParameters("String twitterScreenNameVar");
			List<TwitterAccount> twitterAccounts = (List<TwitterAccount>) query.execute(screenName);
			if (twitterAccounts.size() == 0) {
				return null;
			} else {
				result = twitterAccounts.get(0);
				cache.put(screenName, result);
			}
		}
		return result;
	}
}

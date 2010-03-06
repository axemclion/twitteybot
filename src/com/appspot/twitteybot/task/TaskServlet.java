package com.appspot.twitteybot.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import twitter4j.http.AccessToken;

import com.appspot.twitteybot.datastore.ApplicationProperty;
import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.ui.Pages;

public class TaskServlet extends HttpServlet {

	private static final long serialVersionUID = 2103613622996613432L;

	private static final String TWITTER_ACCOUNT_CACHE = "twitterAccountCache";
	private static final Logger log = Logger.getLogger(TaskServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action == null) {
			return;
		} else if (action.equals(Pages.PARAM_ACTION_UPDATE)) {
			String consumer_key = ApplicationProperty.read(ApplicationProperty.CONSUMER_KEY);
			String consumer_secret = ApplicationProperty.read(ApplicationProperty.CONSUMER_SECRET);
			log.log(Level.INFO, "Consumer Key : " + consumer_key);
			try {
				String twitterScreenName = req.getParameter(Pages.PARAM_STATUS_TWITTER_SCREEN);
				String status = req.getParameter(Pages.PARAM_STATUS_STATUS);
				TwitterAccount twitterAccount = this.getTwitterAccount(twitterScreenName);
				Twitter twitter = new Twitter();
				twitter.setOAuthConsumer(consumer_key, consumer_secret);
				twitter.setOAuthAccessToken(new AccessToken(twitterAccount.getToken(), twitterAccount.getSecret()));
				if (status.length() > 140) {
					status = status.substring(0, 139);
				}
				twitter.updateStatus(status);
				// TODO Catch DeadlineExceededException
				String key = req.getParameter(Pages.PARAM_STATUS_KEY);
				// TODO Add logic to delete this key
				log.log(Level.FINE, "Key of this twitter, that can be deleted is " + key);
			} catch (CacheException e) {
				throw new ServletException(e);
			} catch (TwitterException e) {
				throw new ServletException(e);
			}

		}
	}

	@SuppressWarnings("unchecked")
	private TwitterAccount getTwitterAccount(String screenName) throws CacheException {
		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(TWITTER_ACCOUNT_CACHE);
		if (cache == null) {
			log.info("Twitter Cache not found, creating a new cache");
			cache = cacheManager.getCacheFactory().createCache(Collections.emptyMap());
			cacheManager.registerCache(TWITTER_ACCOUNT_CACHE, cache);
		}
		Object obj = cache.get(screenName);
		TwitterAccount result = null;
		if (obj instanceof TwitterAccount) {
			result = (TwitterAccount) obj;
		}
		if (result == null) {
			log.info("Screenname " + screenName + " not found in cache. Putting it in now");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(TwitterAccount.class);
			query.setFilter("twitterScreenName == twitterScreenNameVar");
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

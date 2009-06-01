package com.appspot.twitteybot.feeds;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.PersistanceFactory;
import com.appspot.twitteybot.datastore.TwitterAccountSettings;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.sun.syndication.feed.synd.SyndEntryImpl;

public class FeedCron
    extends HttpServlet {
    private static final long serialVersionUID = 3266332204576717233L;
    private static final Logger log = Logger.getLogger(FeedCron.class.getName());
    private static final String PARAM_RESET = "loadurl";

    private static final String URL_LIST = "KEY_URL_FEED";
    private static final String BASE_TIMER = "KEY_BASE_TIMER";

    private Cache cache;

    @Override
    public void init() throws ServletException {
        log.log(Level.INFO, "Initiating the FeedReader URLs");

        Map<String, Object> props = new HashMap<String, Object>();
        CacheFactory cacheFactory;
        try {
            cacheFactory = CacheManager.getInstance().getCacheFactory();
            this.cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Cache Error", e);
        }
        this.cache.put(FeedCron.BASE_TIMER, new Date());
        this.loadURLs(true);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        if (req.getParameter(PARAM_RESET) != null || !this.cache.containsKey(URL_LIST)) {
            log.log(Level.INFO, "Loading the URLs again");
            this.loadURLs(true);
        }

        List<URL> feedUrl = new ArrayList<URL>();
        List<TwitterAccountSettings> twitterAccounts = (List<TwitterAccountSettings>)this.cache
            .get(URL_LIST);
        Date startTime = (Date)this.cache.get(BASE_TIMER);
        if (twitterAccounts == null || startTime == null) {
            log.log(Level.SEVERE, "Could not find values in CACHE");
            return;
        }

        for (TwitterAccountSettings twitterAccount : twitterAccounts) {
            feedUrl.add(twitterAccount.getFeedUrl());
        }
        this.fetchFeeds(feedUrl);
    }

    private void fetchFeeds(List<URL> feedUrls) {
        PersistenceManager statusPersistanceManager = PersistanceFactory.getManager();
        for (URL feedUrl : feedUrls) {
            FeedReader reader = new FeedReader(feedUrl);
            for (SyndEntryImpl entry : reader.getItems()) {
                TwitterStatus status = new TwitterStatus(new Date(), entry, feedUrl);
                statusPersistanceManager.makePersistent(status);
                log.log(Level.FINE, status.getStatus().getTitle());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadURLs(boolean forceReload) {

        if (this.cache.containsKey(FeedCron.URL_LIST) && forceReload == false) {
            log.log(Level.FINE, "Found in cache, so not loading. Reload is ", forceReload);
            return;
        }

        List<TwitterAccountSettings> results = (List<TwitterAccountSettings>)PersistanceFactory
            .getManager().getExtent(TwitterAccountSettings.class);
        this.cache.put(URL_LIST, results);
    }
}

package com.appspot.twitteybot.feeds;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.appspot.twitteybot.datastore.helper.FeedConfigHelper;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader {
    private static final Logger log = Logger.getLogger(FeedReader.class.getName());
    private static final String CACHE_NAME = "FeedReaderCache";
    static final String KEY_FEED_URLS = "FeedUrls";
    
    @SuppressWarnings("unchecked")
    public FeedReader() {
	List<FeedConfiguration> feedUrls = this.getFeedUrls();
	if (feedUrls == null) {
	    feedUrls = FeedConfigHelper.dumpAllFeeds();
	    this.getCache().put(FeedReader.KEY_FEED_URLS, feedUrls);
	    log.log(Level.INFO, "Loading URLs from datastore");
	}
    }

    /**
     * Gets the Feed Cache. If the feed Cache is not available, it will be
     * created, registered and returned.
     * 
     * @return Cache where the feed URL will be put in. Created if it does not
     *         exist.
     */
    public Cache getCache() {
	CacheManager cacheManager = CacheManager.getInstance();
	Cache cache = cacheManager.getCache(FeedReader.CACHE_NAME);
	try {
	    if (cache == null) {
		cache = cacheManager.getCacheFactory().createCache(Collections.emptyMap());
		cacheManager.registerCache(FeedReader.CACHE_NAME, cache);
		log.log(Level.INFO, "Cache not found, creating a new cache");
	    }
	} catch (CacheException e) {
	    log.log(Level.SEVERE, "CacheError", e);
	    throw new RuntimeException("Could not create Cache. Cannot continue working");
	}
	return cache;
    }

    @SuppressWarnings("unchecked")
    public List<FeedConfiguration> getFeedUrls() {
	return (List<FeedConfiguration>) this.getCache().get(FeedReader.KEY_FEED_URLS);
    }

    /**
     * Loads the URls from the datastore
     * 
     * @return Map of Feed URLs and
     */

    /**
     * Fetches from a set of active URLs
     * 
     * @param activeFeedURLs
     * @param instance
     */
    public void saveFeeds(List<URL> activeFeedURLs) {
	for (URL feedUrl : activeFeedURLs) {
	    SyndFeedInput input = new SyndFeedInput();
	    try {
		SyndFeed feed = input.build(new XmlReader(feedUrl));
		@SuppressWarnings("unchecked")
		List<SyndEntryImpl> feedList = feed.getEntries();
		for (SyndEntryImpl entry : feedList) {
		    TwitterStatus status = new TwitterStatus(new Date(), entry.getTitle(), feedUrl.toString());
		    log.log(Level.FINER, feedUrl.toString(), status);
		}
	    } catch (IOException e) {
		log.log(Level.WARNING, feedUrl.toExternalForm(), e);
	    } catch (IllegalArgumentException e) {
		log.log(Level.WARNING, feedUrl.toExternalForm(), e);
	    } catch (FeedException e) {
		log.log(Level.WARNING, feedUrl.toExternalForm(), e);
	    }
	}
    }

    /**
     * Updates the URLs in cache with newer configurations
     * 
     * @param configs
     */
    @SuppressWarnings("unchecked")
    public void updateCache(List<FeedConfiguration> configs) {
	this.getCache().put(FeedReader.KEY_FEED_URLS, configs);
    }
}

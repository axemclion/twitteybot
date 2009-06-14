package com.appspot.twitteybot.cron.feeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader {
    private static final Logger log = Logger.getLogger(FeedReader.class.getName());
    private String feedUrl;

    public FeedReader(String config) {
	this.feedUrl = config;
    }

    /**
     * Fetches from a set of active URLs
     * 
     * @param activeFeedURLs
     * @param instance
     */
    public List<String> fetchFeed() {
	List <String> result = new ArrayList<String>();
	URL url = null;
	try {
	    url = new URL(this.feedUrl);
	} catch (MalformedURLException e) {
	    log.log(Level.WARNING, this.feedUrl, e);
	}
	SyndFeedInput input = new SyndFeedInput();
	try {

	    SyndFeed feed = input.build(new XmlReader(url));
	    @SuppressWarnings("unchecked")
	    List<SyndEntryImpl> feedList = feed.getEntries();
	    for (SyndEntryImpl entry : feedList) {
		String status = entry.getTitle();
		log.log(Level.FINER, feedUrl.toString(), status);
		result.add(status);
	    }
	} catch (IOException e) {
	    log.log(Level.WARNING, url.toExternalForm(), e);
	} catch (IllegalArgumentException e) {
	    log.log(Level.WARNING, url.toExternalForm(), e);
	} catch (FeedException e) {
	    log.log(Level.WARNING, url.toExternalForm(), e);
	}
	return result;
    }
}

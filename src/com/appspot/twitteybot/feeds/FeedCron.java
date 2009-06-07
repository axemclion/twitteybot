package com.appspot.twitteybot.feeds;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.FeedConfiguration;

public class FeedCron extends HttpServlet {
    private static final long serialVersionUID = 3266332204576717233L;
    private static final Logger log = Logger.getLogger(FeedCron.class.getName());

    /**
     * Will be called every minute by the Cron Job on the Google app engine
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	FeedReader reader = new FeedReader();
	List<URL> activeFeedURLs = new ArrayList<URL>();
	Calendar currentTime = Calendar.getInstance();

	List<FeedConfiguration> configs = reader.getFeedUrls();
	for (FeedConfiguration config : configs) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(config.getNextUpdate());
	    if (Math.round(cal.getTimeInMillis() / 1000) == Math.round(currentTime.getTimeInMillis() / 1000)) {
		log.log(Level.FINE, "Reading Log", config.getFeedUrl());
		activeFeedURLs.add(new URL(config.getFeedUrl()));
		config.setPreviousUpdate(currentTime.getTime());
		currentTime.add(Calendar.MINUTE, config.getFeedUpdateInterval());
		config.setNextUpdate(currentTime.getTime());
	    }
	}
	reader.updateCache(configs);
	reader.saveFeeds(activeFeedURLs);
    }
}

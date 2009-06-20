package com.appspot.twitteybot.cron.feeds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appspot.twitteybot.cron.Scheduler;
import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.helper.FeedConfigHelper;
import com.appspot.twitteybot.datastore.helper.StatusHelper;
import com.appspot.twitteybot.datastore.helper.TwitterDataHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class FeedCron extends Scheduler {
    private static final String CAHE_KEY = "feed-cache-key";

    private static final String HTTP_URL = "http:";
    private static final String HTTPS_URL = "https";

    @Override
    protected int getUpdateInterval(Object job) {
	return ((FeedConfiguration) job).getFeedUpdateInterval();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void run(List<?> jobs) {
	if (jobs.size() == 0) {
	    return;
	}
	Map<String, List<String>> completedJobs = new HashMap<String, List<String>>();
	for (FeedConfiguration config : (List<FeedConfiguration>) jobs) {
	    if (this.isValidUrl(config.getFeedUrl()) && !completedJobs.containsKey(config.getFeedUrl())) {
		FeedReader reader = new FeedReader(config.getFeedUrl());
		completedJobs.put(config.getFeedUrl(), reader.fetchFeed());
	    }
	}

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    return;
	}

	TwitterDataHelper twitterHelper = new TwitterDataHelper(user);
	for (TwitterAccount account : twitterHelper.getAllTwitterAccounts()) {
	    FeedConfigHelper feedHelper = new FeedConfigHelper(twitterHelper, account.getTwitterName());
	    for (FeedConfiguration config : twitterHelper.getAllFeedConfigurations(account.getTwitterName())) {
		if (completedJobs.containsKey(config.getFeedUrl())) {
		    StatusHelper statusHelper = new StatusHelper(feedHelper, config.getFeedUrl());
		    statusHelper.addStatus(completedJobs.get(config.getFeedUrl()));
		}
	    }
	}
    }

    private boolean isValidUrl(String feedUrl) {
	if (feedUrl.startsWith(HTTP_URL) || feedUrl.startsWith(HTTPS_URL)) {
	    return true;
	} else {
	    return false;
	}
    }

    public FeedCron() {
	this.jobList = FeedConfigHelper.dumpAllFeeds();
	this.cacheKey = CAHE_KEY;
    }

}

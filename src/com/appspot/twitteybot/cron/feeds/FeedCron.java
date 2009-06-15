package com.appspot.twitteybot.cron.feeds;

import java.util.List;

import com.appspot.twitteybot.cron.Scheduler;
import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.helper.FeedConfigHelper;

public class FeedCron extends Scheduler {
    private static final String CAHE_KEY = "feed-cache-key";

    @Override
    protected int getUpdateInterval(Object job) {
	return ((FeedConfiguration) job).getFeedUpdateInterval();
    }

    @Override
    protected void run(List<?> jobs) {
	/*
	 * for (FeedConfiguration config : (List<FeedConfiguration>) jobs) { }
	 */
    }

    public FeedCron() {
	this.jobList = FeedConfigHelper.dumpAllFeeds();
	this.cacheKey = CAHE_KEY;
    }

}

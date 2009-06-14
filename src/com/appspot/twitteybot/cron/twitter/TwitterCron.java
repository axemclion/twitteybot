package com.appspot.twitteybot.cron.twitter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appspot.twitteybot.cron.Scheduler;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.helper.TwitterDataHelper;

public class TwitterCron extends Scheduler {

    private static final Logger log = Logger.getLogger(TwitterCron.class.getName());
    private static final String TWITTER_CACHE_KEY = "twitter-Cache-Key";

    @Override
    protected int getUpdateInterval(Object job) {
	return ((TwitterAccount) job).getTwitterInterval();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void run(List<?> jobs) {
	for (TwitterAccount job : (List<TwitterAccount>) jobs) {
	    log.log(Level.FINE, "Updating status ", job.getUserName());
	    TwitterFacade twitterFacade = new TwitterFacade(job.getUserName(), job.getPassword());
	    twitterFacade.updateStatus("This is a sample status update");
	}
    }

    public TwitterCron() {
	this.jobList = TwitterDataHelper.dumpAllAccounts();
	this.cacheKey = TWITTER_CACHE_KEY;
    }
}

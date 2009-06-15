package com.appspot.twitteybot.cron.twitter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appspot.twitteybot.cron.Scheduler;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.appspot.twitteybot.datastore.helper.StatusHelper;
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
	    StatusHelper helper = new StatusHelper(this.getUser(), job.getTwitterName(), null);
	    TwitterStatus lastStatus = helper.getLastStatus();
	    TwitterFacade twitterFacade = new TwitterFacade(job.getTwitterName(), job.getPassword());
	    if (lastStatus != null) {
		log.log(Level.FINE, "Deleted status ", lastStatus.getStatus());
		twitterFacade.updateStatus(lastStatus.getStatus());
		helper.deleteStatus(lastStatus);
	    }
	    log.log(Level.FINE, "Updating status ", job.getTwitterName());
	}
    }

    public TwitterCron() {
	this.jobList = TwitterDataHelper.dumpAllAccounts();
	this.cacheKey = TWITTER_CACHE_KEY;
    }
}

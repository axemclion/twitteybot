package com.appspot.twitteybot.datastore.helper;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.UserConfig;
import com.google.appengine.api.users.User;

/**
 * Abstracts out the FeedConfigurations data object
 */
public class FeedConfigHelper {

    private TwitterDataHelper twitterHelper;
    private TwitterAccount twitterAccount;

    public FeedConfigHelper(User user, String twitterName) {
	this.twitterHelper = new TwitterDataHelper(user);
	if (twitterHelper != null) {
	    this.twitterAccount = this.twitterHelper.getTwitterAccount(twitterName);
	}
    }

    public TwitterAccount getTwitterAccount() {
	return this.twitterAccount;
    }

    /**
     * Gets all the feed for a given Twitter URL
     * 
     * @return
     */
    public List<FeedConfiguration> getAllFeeds() {
	return this.twitterAccount.getFeedUrls();
    }

    /**
     * Gets the FeedConfiguration give the feed URL
     * 
     * @param parameter
     * @return
     */
    public FeedConfiguration getFeed(String parameter) {
	FeedConfiguration result = null;
	if (this.twitterAccount == null) {
	    return null;
	}
	for (FeedConfiguration feed : this.getTwitterAccount().getFeedUrls()) {
	    if (feed.getFeedUrl().equals(parameter)) {
		result = feed;
		break;
	    }
	}
	return result;
    }

    /**
     * Creates or updates the parameters of a feed
     * 
     * @param feedName
     * @param interval
     * @return
     */
    public boolean setFeedConfig(String feedName, int interval) {
	FeedConfiguration config = this.getFeed(feedName);
	if (config == null) {
	    config = new FeedConfiguration(feedName, interval);
	    this.twitterAccount.getFeedUrls().add(config);
	}
	config.setFeedUpdateInterval(interval);
	return true;
    }

    /**
     * Deletes the give feed
     * 
     * @param feedName
     * @return
     */
    public boolean deleteFeedConfig(String feedName) {
	// TODO Write code to delete feed
	return false;
    }

    /**
     * Commits the changes to the database
     */
    public void commit() {
	this.twitterHelper.commit();
    }

    /**
     * Get a dump of all the available feed. Used to populate it in the Memcache
     * 
     * @return
     */
    public static List<FeedConfiguration> dumpAllFeeds() {
	List<FeedConfiguration> result = new ArrayList<FeedConfiguration>();
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Extent<UserConfig> userConfigs = pm.getExtent(UserConfig.class);
	for (UserConfig config : userConfigs) {
	    for (TwitterAccount twitterAccount : config.getTwitterAccounts()) {
		for (FeedConfiguration feed : twitterAccount.getFeedUrls()) {
		    result.add(feed);
		}
	    }
	}
	return result;
    }

}

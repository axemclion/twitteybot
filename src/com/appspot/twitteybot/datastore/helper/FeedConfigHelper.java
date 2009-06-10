package com.appspot.twitteybot.datastore.helper;

import java.util.List;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.google.appengine.api.users.User;

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

    public List<FeedConfiguration> getAllFeeds() {
	return this.twitterAccount.getFeedUrls();
    }

    public FeedConfiguration getFeed(String parameter) {
	FeedConfiguration result = null;
	for (FeedConfiguration feed : this.getTwitterAccount().getFeedUrls()) {
	    if (feed.getFeedUrl().equals(parameter)) {
		result = feed;
		break;
	    }
	}
	return result;
    }

    public boolean setFeedConfig(String feedName, int interval) {
	FeedConfiguration config = this.getFeed(feedName);
	if (config == null) {
	    config = new FeedConfiguration(feedName, interval);
	    this.twitterAccount.getFeedUrls().add(config);
	}
	config.setFeedUpdateInterval(interval);
	return true;
    }

    public boolean deleteFeedConfig(String feedName) {
	return false;
    }

    public void commit() {
	this.twitterHelper.commit();
    }

}

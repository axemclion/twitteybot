package com.appspot.twitteybot.datastore.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.google.appengine.api.users.User;

public class StatusHelper {
    private String feedUrl;
    private FeedConfigHelper feedHelper;

    public StatusHelper(User user, String twittername, String feedUrl) {
	this.feedHelper = new FeedConfigHelper(user, twittername);
	this.feedUrl = feedUrl;

    }

    public List<TwitterStatus> getAllStatus() {
	return this.getFeedConfig().getStatuses();
    }

    private FeedConfiguration getFeedConfig() {
	return this.feedHelper.getFeed(this.feedUrl);
    }

    /**
     * Adds a twitter status to the database
     * 
     * @param status
     */
    public void addStatus(String statusString) {
	TwitterStatus status = new TwitterStatus(statusString);
	this.getAllStatus().add(status);
    }

    public void addStatus(List<String> asList) {
	for (String status : asList) {
	    this.addStatus(status);
	}
    }

    public void commit() {
	this.feedHelper.commit();
    }

    public TwitterStatus getLastStatus() {
	TwitterStatus result = null;
	List<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
	for (FeedConfiguration config : this.feedHelper.getAllFeeds()) {
	    statuses.addAll(config.getStatuses());
	}
	Calendar lastUpdateTime = Calendar.getInstance();
	for (TwitterStatus status : statuses) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(status.getUpdatedTime());
	    if (lastUpdateTime.after(cal)) {
		result = status;
		lastUpdateTime.setTime(result.getUpdatedTime());
	    }
	}
	return result;
    }

    public void deleteStatus(TwitterStatus status) {
	PersistenceManager pm = PMF.get().getPersistenceManager();
	status = pm.getObjectById(TwitterStatus.class, status.getKey());
	pm.deletePersistent(status);
	pm.close();
    }
}

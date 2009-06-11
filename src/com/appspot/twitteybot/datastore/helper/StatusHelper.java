package com.appspot.twitteybot.datastore.helper;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.twitteybot.datastore.TwitterStatus;

public class StatusHelper {
    private String feedSource;
    private Query query;
    private PersistenceManager pm;

    public StatusHelper(String src) {
	this.feedSource = src;
	this.pm = PMF.get().getPersistenceManager();
	this.query = pm.newQuery(TwitterStatus.class);
    }

    @SuppressWarnings("unchecked")
    public List<TwitterStatus> getAllStatus() {
	this.query.setFilter("feedUrl == url");
	this.query.declareParameters("String url");
	return (List<TwitterStatus>) query.execute(this.feedSource);
    }

    /**
     * Adds a twitter status to the database
     * 
     * @param status
     */
    public void addStatus(String status) {
	if (status.trim() != null) {
	    TwitterStatus twitterStatus = new TwitterStatus(new Date(), status, this.feedSource);
	    this.pm.makePersistent(twitterStatus);
	}
    }

    /**
     * Appends a list of twitter statuses to the database
     * 
     * @param s
     */
    public void addStatus(List<String> s) {
	for (String status : s) {
	    this.addStatus(status);
	}
    }

    public void commit() {
	this.pm.close();
    }

}

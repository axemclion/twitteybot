package com.appspot.twitteybot.datastore;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FeedConfiguration {

    @SuppressWarnings("unused")
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String feedUrl;

    @Persistent
    private int feedUpdateInterval;

    @Persistent
    private Date previousUpdate;

    @Persistent
    private Date nextUpdate;

    public FeedConfiguration(String url, int i) {
	this.feedUpdateInterval = i;
	this.feedUrl = url;
	this.nextUpdate = new Date();
	this.previousUpdate = new Date();
    }

    public String getFeedUrl() {
	return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
	this.feedUrl = feedUrl;
    }

    public int getFeedUpdateInterval() {
	return feedUpdateInterval;
    }

    public void setFeedUpdateInterval(int feedUpdateInterval) {
	this.feedUpdateInterval = feedUpdateInterval;
    }

    public Date getPreviousUpdate() {
	return previousUpdate;
    }

    public void setPreviousUpdate(Date previousUpdate) {
	this.previousUpdate = previousUpdate;
    }

    public Date getNextUpdate() {
	return nextUpdate;
    }

    public void setNextUpdate(Date nextUpdate) {
	this.nextUpdate = nextUpdate;
    }
}

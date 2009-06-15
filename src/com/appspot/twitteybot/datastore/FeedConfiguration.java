package com.appspot.twitteybot.datastore;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FeedConfiguration implements Serializable {

    private static final long serialVersionUID = -6711800941703445288L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String feedUrl;

    @Persistent
    private int feedUpdateInterval;

    @Persistent
    private List<TwitterStatus> statuses;

    public FeedConfiguration(String url, int i) {
	this.feedUpdateInterval = i;
	this.feedUrl = url;
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

    public Key getKey() {
	return key;
    }

    public void setKey(Key key) {
	this.key = key;
    }

    public List<TwitterStatus> getStatuses() {
	return statuses;
    }

    public void setStatusList(List<TwitterStatus> statusList) {
	this.statuses = statusList;
    }
}

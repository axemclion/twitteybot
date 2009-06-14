package com.appspot.twitteybot.datastore;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * Class holding the configuration settings on individual twitter accounts
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterAccount implements Serializable{

    private static final long serialVersionUID = -1625626597178446777L;

    @SuppressWarnings("unused")
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String twitterName;

    @Persistent
    private String password;

    @Persistent
    private int twitterInterval;

    @Persistent
    private List<FeedConfiguration> feedUrls;

    public String getUserName() {
	return twitterName;
    }

    public void setUserName(String userName) {
	this.twitterName = userName;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public int getTwitterInterval() {
	return twitterInterval;
    }

    public void setTwitterInterval(int twitterInterval) {
	this.twitterInterval = twitterInterval;
    }

    public List<FeedConfiguration> getFeedUrls() {
	return feedUrls;
    }

    public void setFeedUrls(List<FeedConfiguration> feedUrl) {
	this.feedUrls = feedUrl;
    }

}

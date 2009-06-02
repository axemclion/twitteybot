package com.appspot.twitteybot.datastore;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Class holding the configuration settings on individual twitter accounts
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterAccount {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String userName;

    @Persistent
    private String password;

    @Persistent
    private Long twitterInterval;

    @Persistent
    private List<FeedConfiguration> feedUrls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTwitterInterval() {
        return twitterInterval;
    }

    public void setTwitterInterval(Long twitterInterval) {
        this.twitterInterval = twitterInterval;
    }

    public List<FeedConfiguration> getFeedUrls() {
        return feedUrls;
    }

    public void setFeedUrls(List<FeedConfiguration> feedUrl) {
        this.feedUrls = feedUrl;
    }

}

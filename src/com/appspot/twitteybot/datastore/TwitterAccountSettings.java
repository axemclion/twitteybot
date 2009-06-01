package com.appspot.twitteybot.datastore;

import java.net.URL;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Class holding the configuration settings on individual twitter accounts
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterAccountSettings {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String userName;

    @Persistent
    private String password;

    @Persistent
    private URL feedUrl;

    @Persistent
    private Long feedInterval;

    @Persistent
    private Long twitterInterval;

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

    public URL getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(URL feedUrl) {
        this.feedUrl = feedUrl;
    }

    public Long getFeedInterval() {
        return feedInterval;
    }

    public void setFeedInterval(Long feedInterval) {
        this.feedInterval = feedInterval;
    }

    public Long getTwitterInterval() {
        return twitterInterval;
    }

    public void setTwitterInterval(Long twitterInterval) {
        this.twitterInterval = twitterInterval;
    }

}

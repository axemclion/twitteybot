package com.appspot.twitteybot.datastore;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Link;

public class TwitterSettings {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String userName;

    @Persistent
    private String password;

    @Persistent
    private Map<Link, Long> feeds;

    @Persistent
    private Long updateInterval;

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

    public Map<Link, Long> getFeeds() {
        return feeds;
    }

    public void setFeeds(Map<Link, Long> feeds) {
        this.feeds = feeds;
    }

    public Long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(Long updateInterval) {
        this.updateInterval = updateInterval;
    }

}

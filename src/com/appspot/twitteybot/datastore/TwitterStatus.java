package com.appspot.twitteybot.datastore;

import java.net.URL;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.sun.syndication.feed.synd.SyndEntryImpl;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterStatus {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Date updatedTime;

    @Persistent
    private SyndEntryImpl status;

    private URL feedUrl;

    public TwitterStatus(Date updateTime, SyndEntryImpl status, URL url) {
        this.feedUrl = url;
        this.updatedTime = updateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public SyndEntryImpl getStatus() {
        return status;
    }

    public void setStatus(SyndEntryImpl status) {
        this.status = status;
    }

    public URL getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(URL feedUrl) {
        this.feedUrl = feedUrl;
    }
}

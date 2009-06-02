package com.appspot.twitteybot.datastore;

import java.net.URL;
import java.util.Calendar;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FeedConfiguration {

    @Persistent
    private URL feedUrl;

    @Persistent
    private int feedUpdateInterval;

    @Persistent
    private Calendar previousUpdate;

    @Persistent
    private Calendar nextUpdate;

    public URL getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(URL feedUrl) {
        this.feedUrl = feedUrl;
    }

    public int getFeedUpdateInterval() {
        return feedUpdateInterval;
    }

    public void setFeedUpdateInterval(int feedUpdateInterval) {
        this.feedUpdateInterval = feedUpdateInterval;
    }

    public Calendar getPreviousUpdate() {
        return previousUpdate;
    }

    public void setPreviousUpdate(Calendar previousUpdate) {
        this.previousUpdate = previousUpdate;
    }

    public Calendar getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Calendar nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

}

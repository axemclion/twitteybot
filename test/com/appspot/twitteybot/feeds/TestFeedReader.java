package com.appspot.twitteybot.feeds;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.SyndEntryImpl;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestFeedReader {

    private static final Logger log = Logger.getLogger(TestFeedReader.class.getName());
    private static final String feedUrl = "http://rss.news.yahoo.com/rss/topstories";

    @Test
    public void testGetItems() {
        FeedReader reader = new FeedReader(feedUrl);
        for (SyndEntryImpl item : reader.getItems()){
            log.log(Level.INFO, item.getTitle());
            assertNotNull(item);
        }
        assertNotNull(reader);
    }
}

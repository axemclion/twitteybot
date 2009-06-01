package com.appspot.twitteybot.feeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader {
    private URL feedUrl;
    private static final Logger log = Logger.getLogger(FeedReader.class.getName());

    public FeedReader(final URL url) {
        this.feedUrl = url;
    }

    public List<SyndEntryImpl> getItems() {

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        List<SyndEntryImpl> entries = null;
        try {
            feed = input.build(new XmlReader(this.feedUrl));
            entries = feed.getEntries();
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, this.feedUrl.toString(), e);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, this.feedUrl.toString(), e);
        } catch (FeedException e) {
            log.log(Level.SEVERE, this.feedUrl.toString(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, this.feedUrl.toString(), e);
        }
        return entries;
    }
}

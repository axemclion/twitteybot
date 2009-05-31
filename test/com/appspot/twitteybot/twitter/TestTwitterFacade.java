package com.appspot.twitteybot.twitter;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestTwitterFacade {

    private static final String username = "twitteybot";
    private static final String password = "Welcome1";

    @Test
    public void testUpdateStatus() {
        TwitterFacade twitter = new TwitterFacade(username, password);

        assertTrue(twitter.updateStatus("This is a sample statement updated at " + new Date()));

    }
}

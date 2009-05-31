package com.appspot.twitteybot.twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

public class TwitterFacade {

    private static final String statusURL = "http://twitter.com/statuses/update.xml";
    private static final Logger log = Logger.getLogger(TwitterFacade.class.getName());

    private String login; // = "twitteybot";
    private String password; // = "Welcome1";

    public TwitterFacade(final String username, final String password) {
        this.login = username;
        this.password = password;
        log.log(Level.CONFIG, "Twitter account with ", username);
    }

    public boolean updateStatus(String statusText) {
        String userPassword = this.login + ":" + this.password;
        String encodedCredentials = new String(new Base64().encode(userPassword.getBytes()));
        boolean status = false;

        try {
            URL url = new URL(TwitterFacade.statusURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("status=" + this.sanitize(statusText));
            writer.close();
            if(connection.getResponseCode() == 200)
            	status = true;
            log.log(Level.INFO, this.login, statusText);

        } catch (Exception e) {
            status = false;
            log.log(Level.WARNING, this.login, e);
        }
        return status;
    }

    private String sanitize(final String status) {
        return status;
    }
}

package com.twitter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TwitBot {

	private String twitterhost = "http://www.twitter.com";
	private String login = "twitteybot";
	private String password = "Welcome1";

	public void twitter(String status) throws URIException, IOException,
			HttpException {
		String statusURL = "/statuses/update.xml";
		String userPassword = login + ":" + password;
		String encoding = new String(new Base64().encode(userPassword.getBytes()));
		URL url = new URL(twitterhost + statusURL);
		HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
		openConnection.setRequestProperty ("Authorization", "Basic " + encoding);
		openConnection.setDoOutput(true);
		openConnection.setRequestMethod("POST");
		OutputStreamWriter writer = new OutputStreamWriter(openConnection.getOutputStream());
        writer.write("status=" + status);
        writer.close();
	}

	public String getFeed() throws FeedException, IOException,
			MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(new URL(
				"http://rss.news.yahoo.com/rss/topstories")));
		List<SyndEntryImpl> list = feed.getEntries();
		return list.get(0).getTitleEx().getValue();
	}
}

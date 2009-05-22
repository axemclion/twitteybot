package com.twitter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TwitBot {

	private static String twitterhost = "www.twitter.com";

	public static void main(String args[]) throws Exception {

		while (true) {
			String feed = getFeed();
			System.out.println(feed);
			if (feed != null)
				twitter(feed);
			Thread.sleep(1000*120);
		}
	}

	private static void twitter(String status) throws URIException,
			IOException, HttpException {
		HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
		String url = "/statuses/update.xml";
		PostMethod method = new PostMethod(url);
		Credentials credentials = new UsernamePasswordCredentials("twitteybot",
				"Welcome1");
		client.getState().setCredentials(
				new AuthScope(twitterhost, 80, AuthScope.ANY_REALM),
				credentials);
		NameValuePair params[] = new NameValuePair[1];
		params[0] = new NameValuePair("status", status);
		method.setRequestBody(params);
		HostConfiguration host = client.getHostConfiguration();
		host.setHost(new URI("http://" + twitterhost, true));
		client.executeMethod(host, method);
	}

	private static String getFeed() throws FeedException, IOException,
			MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(new URL(
				"http://rss.news.yahoo.com/rss/topstories")));
		List<SyndEntryImpl> list = feed.getEntries();
		return list.get(0).getTitleEx().getValue();
	}
}

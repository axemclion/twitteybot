package com.appspot.twitteybot.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.helper.FeedConfigHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class FeedConfigManager extends HttpServlet {
    private static final long serialVersionUID = 1403209552616038741L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a registered user to perform this operation");
	    return;
	}

	String twitterName = req.getParameter(Pages.PARAM_TWITTER);
	String action = req.getParameter(Pages.PARAM_ACTION);
	FeedConfigHelper feedHelper = new FeedConfigHelper(user, twitterName);
	if (feedHelper.getTwitterAccount() == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Twitter Account not found for your user name. "
		    + twitterName);
	    return;
	}

	if (action == null) {
	    action = Pages.ACTION_EDIT;
	}

	if (action.equals(Pages.ACTION_SHOW)) {
	    List<String> feedUrls = new ArrayList<String>();
	    for (FeedConfiguration feed : feedHelper.getAllFeeds()) {
		feedUrls.add(feed.getFeedUrl());
	    }
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(Pages.FTLVAR_FEED_URLS, feedUrls);
	    props.put(Pages.FTLVAR_PAGE_FEEDS, Pages.PAGE_FEEDS + Pages.PARAM_TWITTER + "="
		    + feedHelper.getTwitterAccount().getUserName());
	    props.put(Pages.FTLVAR_PAGE_UPLOAD, Pages.PAGE_UPLOAD_STATUS);
	    props.put(Pages.PARAM_TWITTER, feedHelper.getTwitterAccount().getUserName());
	    props.put(Pages.FTLVAR_PAGE_STATUS, Pages.PAGE_STATUS);
	    FreeMarkerConfiguration.writeResponse(props, Pages.TEMPLATE_SHOW_FEED, resp.getWriter());
	} else if (action.equals(Pages.ACTION_EDIT)) {
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(Pages.PARAM_FEED_NAME, Pages.PARAM_FEED_NAME);
	    props.put(Pages.PARAM_INTERVAL, Pages.PARAM_INTERVAL);

	    FeedConfiguration configuration = feedHelper.getFeed(req.getParameter(Pages.PARAM_FEED_NAME));
	    if (configuration != null) {
		props.put("feedNameValue", configuration.getFeedUrl());
		props.put("intervalValue", configuration.getFeedUpdateInterval());
	    }
	    props.put("targetUrl", Pages.PAGE_FEEDS + Pages.PARAM_ACTION + "=" + Pages.ACTION_ADD + "&"
		    + Pages.PARAM_TWITTER + "=" + feedHelper.getTwitterAccount().getUserName());
	    FreeMarkerConfiguration.writeResponse(props, Pages.TEMPLATE_ADD_FEED, resp.getWriter());
	} else if (action.equals(Pages.ACTION_ADD)) {
	    String feedName = req.getParameter(Pages.PARAM_FEED_NAME);
	    int interval = Integer.parseInt(req.getParameter(Pages.PARAM_INTERVAL));
	    if (!feedHelper.setFeedConfig(feedName, interval)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or edit feed " + feedName);
		return;
	    }
	} else if (action.equals(Pages.ACTION_DELETE)) {
	    String feedName = req.getParameter(Pages.PARAM_FEED_NAME);
	    if (!feedHelper.deleteFeedConfig(feedName)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or edit feed " + feedName);
		return;
	    }
	}
	feedHelper.commit();
    }
}

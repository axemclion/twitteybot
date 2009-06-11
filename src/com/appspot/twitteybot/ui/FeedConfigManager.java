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

    static final String PARAM_TWITTER_NAME = "twitter";
    static final String PARAM_FEED_NAME = "feedname";
    static final String PARAM_INTERVAL = "interval";

    private static final String PARAM_ACTION = "action";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_ADD = "add";
    private static final String ACTION_SHOW = "show";
    private static final String ACTION_DELETE = "delete";

    private static final String SHOW_FEED_FTL = "ShowFeeds.ftl";
    private static final String ADD_FEED_FTL = "AddFeeds.ftl";

    private static final String FTL_FEED_URLS = "feeds";
    private static final String FTL_PAGE_FEEDS = "feedsPage";
    private static final String FTL_PAGE_UPLOAD = "uploadPage";
    private static final String FTL_PAGE_STATUS = "statusPage";

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

	String twitterName = req.getParameter(PARAM_TWITTER_NAME);
	String action = req.getParameter(PARAM_ACTION);
	FeedConfigHelper feedHelper = new FeedConfigHelper(user, twitterName);
	if (feedHelper.getTwitterAccount() == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Twitter Account not found for your user name. "
		    + twitterName);
	    return;
	}

	if (action == null) {
	    action = ACTION_EDIT;
	}

	if (action.equals(ACTION_SHOW)) {
	    List<String> feedUrls = new ArrayList<String>();
	    for (FeedConfiguration feed : feedHelper.getAllFeeds()) {
		feedUrls.add(feed.getFeedUrl());
	    }
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(FTL_FEED_URLS, feedUrls);
	    props.put(FTL_PAGE_FEEDS, Pages.PAGE_FEEDS + PARAM_TWITTER_NAME + "="
		    + feedHelper.getTwitterAccount().getUserName());
	    props.put(FTL_PAGE_UPLOAD, Pages.PAGE_UPLOAD_STATUS);
	    props.put(PARAM_TWITTER_NAME, feedHelper.getTwitterAccount().getUserName());
	    props.put(FTL_PAGE_STATUS, Pages.PAGE_STATUS);
	    FreeMarkerConfiguration.writeResponse(props, SHOW_FEED_FTL, resp.getWriter());
	} else if (action.equals(ACTION_EDIT)) {
	    Map<String, Object> props = new HashMap<String, Object>();
	    props.put(PARAM_FEED_NAME, PARAM_FEED_NAME);
	    props.put(PARAM_INTERVAL, PARAM_INTERVAL);

	    FeedConfiguration configuration = feedHelper.getFeed(req.getParameter(PARAM_FEED_NAME));
	    if (configuration != null) {
		props.put("feedNameValue", configuration.getFeedUrl());
		props.put("intervalValue", configuration.getFeedUpdateInterval());
	    }
	    props.put("targetUrl", Pages.PAGE_FEEDS + PARAM_ACTION + "=" + ACTION_ADD + "&" + PARAM_TWITTER_NAME + "="
		    + feedHelper.getTwitterAccount().getUserName());
	    FreeMarkerConfiguration.writeResponse(props, ADD_FEED_FTL, resp.getWriter());
	} else if (action.equals(ACTION_ADD)) {
	    String feedName = req.getParameter(PARAM_FEED_NAME);
	    int interval = Integer.parseInt(req.getParameter(PARAM_INTERVAL));
	    if (!feedHelper.setFeedConfig(feedName, interval)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or edit feed " + feedName);
		return;
	    }
	} else if (action.equals(ACTION_DELETE)) {
	    String feedName = req.getParameter(PARAM_FEED_NAME);
	    if (!feedHelper.deleteFeedConfig(feedName)) {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not add or edit feed " + feedName);
		return;
	    }
	}
	feedHelper.commit();
    }
}

package com.appspot.twitteybot.ui;

public class Pages {
    public static final String LANDING_PAGE = "/jsp/showInfo.jsp?loginUrl=";

    public static final String PAGE_MAIN = "/pages/main?";
    public static final String PAGE_TWITTER_ACCOUNTS = "/pages/twitter?";
    public static final String PAGE_FEEDS = "/pages/feeds?";
    public static final String PAGE_UPLOAD_STATUS = "/pages/upload?";
    public static final String PAGE_STATUS = "/pages/status?";

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_SHOW = "show";
    public static final String ACTION_DELETE = "delete";

    public static final String PARAM_CRON_JOB = "job";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_USER_NAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_INTERVAL = "interval";
    public static final String PARAM_SEPARATOR = "seperator";
    public static final String PARAM_TWITTER = "twitter";
    public static final String PARAM_SRC_NAME = "feedname";
    public static final String PARAM_FEED_NAME = "feedname";

    public static final String FTLVAR_TWITTER_ACCOUNTS = "accounts";
    public static final String FTLVAR_PAGE_TWITTER = "twitterPage";
    public static final String FTLVAR_PAGE_FEEDS = "feedsPage";
    public static final String FTLVAR_USERNAME = "username";
    public static final String FTLVAR_STATUS = "status";
    public static final String FTLVAR_FEED_URLS = "feeds";
    public static final String FTLVAR_PAGE_UPLOAD = "uploadPage";
    public static final String FTLVAR_PAGE_STATUS = "statusPage";

    public static final String FTLVAR_USERNAME_VALUE = "usernameValue";
    public static final String FTLVAR_INTERVAL_VALUE = "intervalValue";
    public static final String FTLVAR_TARGET_URL = "targetUrl";

    public static final String TEMPLATE_ADD_ACCOUNT = "AddTwitterAccount.ftl";
    public static final String TEMPlATE_SHOW_ACCOUNT = "ShowAccounts.ftl";
    public static final String TEMPLATE_MAIN_PAGE = "MainPage.ftl";
    public static final String TEMPLATE_SHOW_STATUS = "ShowStatus.ftl";
    public static final String TEMPLATE_SHOW_FEED = "ShowFeeds.ftl";
    public static final String TEMPLATE_ADD_FEED = "AddFeeds.ftl";

    public static final String CRON_TWITTER = "twitter";
    public static final String CRON_FEED = "feed";

}

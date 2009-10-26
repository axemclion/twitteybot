package com.appspot.twitteybot.ui;

public class Pages {
	public static final String TEMPLATE_MAIN_PAGE = "MainPage.ftl";
	public static final String TEMPLATE_STATUSPAGE = "ShowStatus.ftl";

	public static final String PARAM_SEPARATOR = "separator";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_VALUE = "value";
	public static final String PARAM_TWITTER_NAME = "twitterAccount";

	public static final String PARAM_ACTION = "action";
	public static final String PARAM_ACTION_DELETE = "Delete";
	public static final String PARAM_ACTION_ADD = "Add";
	public static final String PARAM_ACTION_UPDATE = "Update";
	public static final String PARAM_ACTION_SHOW = "Show";
	public static final String PARAM_ACTION_UPLOAD = "Upload";
	public static final String PARAM_ACTION_FETCH = "fetch";

	public static final String PARAM_STATUS_CANADD = "item_";
	public static final String PARAM_TOTAL_ITEMS = "totalItems";
	public static final String PARAM_STATUS_UPDATE_DATE = "updatedTime_";
	public static final String PARAM_STATUS_SOURCE = "source_";
	public static final String PARAM_STATUS_CAN_DELETE = "canDelete_";
	public static final String PARAM_STATUS_STATUS = "status_";
	public static final String PARAM_STATUS_TWITTER_SCREEN = "twitterScreenName_";
	public static final String PARAM_STATUS_KEY = "key_";

	public static final String PARAM_OAUTH = "oauth";
	public static final String PARAM_OAUTH_TOKEN = "oauth_token";
	public static final String PARAM_TOKEN = "token";
	public static final String PARAM_SCREENNAME = "screenName";

	public static final String PARAM_START = "start";
	public static final String PARAM_END = "end";

	public static final String PAGE_HOME = "/?";
	public static final String PAGE_MAIN = "/pages/main?";
	public static final String PAGE_TASK_QUEUE = "/task/status";

	public static final String FTLVAR_TWITTER_STATUS = "statuses";
	public static final String FTLVAR_TWITTER_ACCOUNTS = "accounts";
	public static final String FTLVAR_USERNAME = "username";
	public static final String FTLVAR_LOGOUT = "logoutUrl";
	public static final String FTLVAR_MESSAGE = "message";
	public static final String FTLVAR_LEVEL = "level";
	public static final String FTLVAR_START = "start";
	public static final String FTLVAR_END = "end";
}
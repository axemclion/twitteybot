package com.appspot.twitteybot.ui;

public class Pages {
	public static final String TEMPLATE_MAIN_PAGE = "MainPage.ftl";
	public static final String TEMPLATE_STATUSPAGE = "ShowStatus.ftl";

	public static final String PARAM_SEPARATOR = "separator";

	public static final String PARAM_TWITTER_NAME = "twitterAccount";

	public static final String PARAM_ACTION = "action";
	public static final String PARAM_ACTION_DELETE = "delete";
	public static final String PARAM_ACTION_ADD = "add";
	public static final String PARAM_ACTION_UPDATE = "update";
	public static final String PARAM_ACTION_SHOW = "show";
	public static final String PARAM_ACTION_UPLOAD = "upload";

	public static final String PARAM_STATUS_CANADD = "item_";
	public static final String PARAM_TOTAL_ITEMS = "totalItems";
	public static final String PARAM_STATUS_UPDATE_DATE = "updatedTime_";
	public static final String PARAM_STATUS_SOURCE = "source_";
	public static final String PARAM_STATUS_CAN_DELETE = "canDelete_";
	public static final String PARAM_STATUS_STATUS = "status_";
	public static final String PARAM_STATUS_TWITTER_SCREEN = "twitterScreenName_";

	public static final String PARAM_OAUTH = "oauth";
	public static final String PARAM_OAUTH_TOKEN = "oauth_token";
	public static final String PARAM_TOKEN = "token";
	public static final String PARAM_SCREENNAME = "screenName";

	public static final String PAGE_HOME = "/?";
	public static final String PAGE_MAIN = "/pages/main?";
	public static final String PAGE_UPLOAD_STATUS = "/pages/upload?";

	public static final String FTLVAR_TWITTER_STATUS = "statuses";
	public static final String FTLVAR_TWITTER_ACCOUNTS = "accounts";
	public static final String FTLVAR_USERNAME = "username";
	public static final String FTLVAR_LOGOUT = "logoutUrl";


}
package com.appspot.twitteybot.cron;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.cron.feeds.FeedCron;
import com.appspot.twitteybot.cron.twitter.TwitterCron;
import com.appspot.twitteybot.ui.Pages;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class CronTrigger extends HttpServlet {
    private static final long serialVersionUID = -6078204805771806315L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a registered user to perform this operation");
	    return;
	}

	String cronName = req.getParameter(Pages.PARAM_CRON_JOB);

	Scheduler scheduler = null;
	if (cronName != null && cronName.equals(Pages.CRON_FEED)) {
	    scheduler = Scheduler.getInstance(FeedCron.class);
	} else {
	    scheduler = Scheduler.getInstance(TwitterCron.class);
	}
	scheduler.setUser(user);
	scheduler.trigger();
    }
}

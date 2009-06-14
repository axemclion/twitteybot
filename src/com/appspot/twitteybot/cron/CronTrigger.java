package com.appspot.twitteybot.cron;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.cron.feeds.FeedCron;
import com.appspot.twitteybot.cron.twitter.TwitterCron;
import com.appspot.twitteybot.ui.Pages;

public class CronTrigger extends HttpServlet {
    private static final long serialVersionUID = -6078204805771806315L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	String cronName = req.getParameter(Pages.PARAM_CRON_JOB);

	Scheduler scheduler = null;
	if (cronName != null && cronName.equals(Pages.CRON_TWITTER)) {
	    scheduler = Scheduler.getInstance(TwitterCron.class);
	} else {
	    scheduler = Scheduler.getInstance(FeedCron.class);
	}

	scheduler.trigger();
    }
}

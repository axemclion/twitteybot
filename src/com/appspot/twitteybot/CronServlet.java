package com.appspot.twitteybot;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.appspot.twitteybot.ui.FreeMarkerConfiguration;
import com.appspot.twitteybot.ui.Pages;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

public class CronServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("updatedTime >= minTime && updatedTime < maxTime");
		query.declareParameters("java.util.Date minTime, java.util.Date maxTime");
		Calendar minTime = Calendar.getInstance();
		Calendar maxTime = Calendar.getInstance();
		minTime.set(Calendar.SECOND, 0);
		minTime.set(Calendar.MILLISECOND, 0);
		maxTime.add(Calendar.MINUTE, 1);
		maxTime.set(Calendar.SECOND, 0);
		maxTime.set(Calendar.MILLISECOND, 0);
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(minTime.getTime(), maxTime
				.getTime());

		Queue queue = QueueFactory.getDefaultQueue();
		for (TwitterStatus twitterStatus : twitterStatuses) {
			queue.add(url("/task/status").param(Pages.PARAM_ACTION, Pages.PARAM_ACTION_UPDATE).param(
					Pages.PARAM_STATUS_TWITTER_SCREEN, twitterStatus.getTwitterScreenName()).param(
					Pages.PARAM_STATUS_STATUS, twitterStatus.getStatus()));
		}

		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, twitterStatuses);
		templateValues.put(Pages.PARAM_ACTION, Pages.PARAM_ACTION_SHOW);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
	}
}

package com.appspot.twitteybot.cron;

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
import com.appspot.twitteybot.datastore.TwitterStatus.State;
import com.appspot.twitteybot.ui.FreeMarkerConfiguration;
import com.appspot.twitteybot.ui.Pages;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Builder;

public class CronServlet extends HttpServlet {

	private static final long serialVersionUID = -7767523786982743018L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("updatedTime < maxTime && state == 'SCHEDULED'");
		query.declareParameters("java.util.Date maxTime, ");
		Calendar maxTime = Calendar.getInstance();
		maxTime.add(Calendar.MINUTE, 1);
		maxTime.set(Calendar.SECOND, 0);
		maxTime.set(Calendar.MILLISECOND, 0);
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(maxTime.getTime());

		Queue queue = QueueFactory.getDefaultQueue();
		int i = 0;
		TaskOptions taskOption = Builder.url(Pages.PAGE_TASK_QUEUE);
		taskOption.param(Pages.PARAM_ACTION, Pages.PARAM_ACTION_UPDATE);
		for (TwitterStatus twitterStatus : twitterStatuses) {
			taskOption.param(Pages.PARAM_STATUS_TWITTER_SCREEN + i, twitterStatus.getTwitterScreenName());
			taskOption.param(Pages.PARAM_STATUS_STATUS + i, twitterStatus.getStatus());
			twitterStatus.setState(State.QUEUED);
			i++;
		}
		taskOption.param(Pages.PARAM_TOTAL_ITEMS, i + "");
		queue.add(taskOption);
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, twitterStatuses);
		templateValues.put(Pages.PARAM_ACTION, Pages.PARAM_ACTION_SHOW);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());

		query.closeAll();
		pm.makePersistentAll(twitterStatuses);
		pm.close();

	}
}

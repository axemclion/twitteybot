package com.appspot.twitteybot.cron;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.appspot.twitteybot.datastore.TwitterStatus.State;
import com.appspot.twitteybot.ui.Pages;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

public class CronServlet extends HttpServlet {

	private static final long serialVersionUID = -7767523786982743018L;
	private static final Logger log = Logger.getLogger(CronServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("updatedTime < maxTime && state == 'SCHEDULED'");
		query.setOrdering("updatedTime");
		query.declareParameters("java.util.Date maxTime, ");
		Calendar maxTime = Calendar.getInstance();
		maxTime.add(Calendar.MINUTE, 1);
		maxTime.set(Calendar.SECOND, 0);
		maxTime.set(Calendar.MILLISECOND, 0);
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(maxTime.getTime());
		PrintWriter out = resp.getWriter();
		Queue queue = QueueFactory.getDefaultQueue();
		for (TwitterStatus twitterStatus : twitterStatuses) {
			TaskOptions taskOption = Builder.url(Pages.PAGE_TASK_QUEUE);
			taskOption.method(Method.POST);
			taskOption.param(Pages.PARAM_ACTION, Pages.PARAM_ACTION_UPDATE);
			taskOption.param(Pages.PARAM_STATUS_TWITTER_SCREEN, twitterStatus.getTwitterScreenName());
			taskOption.param(Pages.PARAM_STATUS_KEY, twitterStatus.getEncodedKey());
			taskOption.param(Pages.PARAM_STATUS_STATUS, twitterStatus.getStatus());
			twitterStatus.setState(State.QUEUED);
			queue.add(taskOption);

			out.write(Pages.PAGE_TASK_QUEUE + "?");
			out.write("&" + Pages.PARAM_ACTION + "=" + Pages.PARAM_ACTION_UPDATE);
			out.write("&" + Pages.PARAM_STATUS_TWITTER_SCREEN + "=" + twitterStatus.getTwitterScreenName());
			out.write("&" + Pages.PARAM_STATUS_KEY + "=" + twitterStatus.getEncodedKey());
			out.write("&" + Pages.PARAM_STATUS_STATUS + "=" + twitterStatus.getStatus());
			out.println();
		}
		query.closeAll();
		pm.makePersistentAll(twitterStatuses);
		pm.close();
	}
}

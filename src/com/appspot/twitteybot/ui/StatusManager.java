package com.appspot.twitteybot.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Responsible for parsing a file that is uploaded with the status messages.
 * Parses the file and presents it to the user to allow modification
 */
public class StatusManager extends HttpServlet {

	private static final Logger log = Logger.getLogger(StatusManager.class.getName());
	private static final long serialVersionUID = 1551252388567429753L;
	private static final int DEFAULT_TIME_INCREMENT = 60;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_UPLOAD)) {
			this.processUpload(req, resp);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_FETCH)) {
			this.processFetch(req, resp);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_ADD)) {
			this.processAdd(req, resp);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_DELETE)) {
			this.processUpdate(req, resp, true);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_UPDATE)) {
			this.processUpdate(req, resp, false);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_SHOW)) {
			this.processShow(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}

	private void processShow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		this.constructResponse(this.getTwitterStatus(req.getParameter(Pages.PARAM_SCREENNAME), pm), "", "",
				resp);
		pm.close();
	}

	private void processUpdate(HttpServletRequest req, HttpServletResponse resp, boolean delete)
			throws IOException {
		int totalItems = Integer.parseInt(req.getParameter(Pages.PARAM_TOTAL_ITEMS));
		PersistenceManager pm = PMF.get().getPersistenceManager();
		User user = UserServiceFactory.getUserService().getCurrentUser();
		List<TwitterStatus> twitterStatuses = new ArrayList<TwitterStatus>();
		List<TwitterStatus> toAddStatuses = new ArrayList<TwitterStatus>();
		String message = null;
		String level = "info";
		for (int i = 0; i <= totalItems; i++) {
			if (this.getBoolFromParam(req.getParameter(Pages.PARAM_STATUS_CANADD + i), "on")) {
				String id = req.getParameter(Pages.PARAM_STATUS_KEY + i);
				TwitterStatus twitterStatus = null;
				if (id.equals("")) {
					twitterStatus = new TwitterStatus();
					twitterStatus.setUser(UserServiceFactory.getUserService().getCurrentUser());
					twitterStatus.setCanDelete(true);
					twitterStatus.setTwitterScreenName(req.getParameter(Pages.PARAM_SCREENNAME));
					twitterStatus.setState(TwitterStatus.State.SCHEDULED);
					toAddStatuses.add(twitterStatus);
				} else {
					Key key = KeyFactory.createKey(TwitterStatus.class.getSimpleName(), Long.parseLong(id
							.replace(",", "")));
					twitterStatus = pm.getObjectById(TwitterStatus.class, key);
					twitterStatuses.add(twitterStatus);
				}
				if (twitterStatus != null && user.getEmail().equals(twitterStatus.getUser().getEmail())) {
					if (!delete) {
						try {
							twitterStatus.setTime(req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i));
						} catch (RuntimeException e) {
							message = "There were errors parsing the time for tweets. Some tweets were not updated.";
							level = "warn";
							continue;
						}
						twitterStatus.setSource(req.getParameter(Pages.PARAM_STATUS_SOURCE + i));
						twitterStatus.setStatus(req.getParameter(Pages.PARAM_STATUS_STATUS + i));
					}
				}
			}
		}

		pm.makePersistentAll(toAddStatuses);
		if (delete) {
			message = twitterStatuses.size() + " Tweets successfully deleted";
			pm.deletePersistentAll(twitterStatuses);
		} else {
			if (message == null) {
				message = twitterStatuses.size() + " Tweets successfully updated";
				if (toAddStatuses.size() > 0) {
					message += ", " + toAddStatuses.size() + " Tweets added.";
				}
			}
		}

		pm.close();
		pm = PMF.get().getPersistenceManager();
		this.constructResponse(this.getTwitterStatus(req.getParameter(Pages.PARAM_SCREENNAME), pm), message,
				level, resp);

		pm.close();
	}

	private void processAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int totalItems = Integer.parseInt(req.getParameter(Pages.PARAM_TOTAL_ITEMS));
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String screenName = req.getParameter(Pages.PARAM_SCREENNAME);
		User user = UserServiceFactory.getUserService().getCurrentUser();

		String message = null;
		String level = "info";

		if (screenName == null || screenName == "") {
			message = "Twitter Screen Name was null and hence, could not add tweets";
			level = "error";
			log.log(Level.SEVERE, "ScerenName supplied was null");
		} else {
			List<TwitterStatus> twitterStatuses = new ArrayList<TwitterStatus>();
			int failedTweetCount = 0;
			for (int i = 0; i <= totalItems; i++) {
				if (this.getBoolFromParam(req.getParameter(Pages.PARAM_STATUS_CANADD + i), "on")) {
					try {
						twitterStatuses.add(new TwitterStatus(user, screenName, req
								.getParameter(Pages.PARAM_STATUS_SOURCE + i), req
								.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i), req
								.getParameter(Pages.PARAM_STATUS_STATUS + i), this.getBoolFromParam(req
								.getParameter(Pages.PARAM_STATUS_CAN_DELETE + i), "on")));
					} catch (RuntimeException e) {
						message = "There were errors parsing the time for tweets." + (++failedTweetCount)
								+ " tweets were not added.";
						level = "warn";
						log
								.log(Level.WARNING, "Could not add "
										+ req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i)
										+ " as parsing failed");
					}
				}
			}
			if (message == null) {
				message = twitterStatuses.size() + " Tweets Successfully added to this account.";
			}
			pm.makePersistentAll(twitterStatuses);
		}
		this.constructResponse(this.getTwitterStatus(screenName, pm), message, level, resp);
		pm.close();
	}

	private void processFetch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String twitterScreenName = req.getParameter(Pages.PARAM_SCREENNAME);
		String fileLocation = req.getParameter(Pages.PARAM_STATUS_SOURCE);
		String message = null;
		String level = "info";
		User user = UserServiceFactory.getUserService().getCurrentUser();
		Date startDate = new Date();
		List<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
		try {
			URL url = new URL(fileLocation);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			int increment = 0;
			while ((line = reader.readLine()) != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.add(Calendar.MINUTE, increment += DEFAULT_TIME_INCREMENT);
				statuses.add(new TwitterStatus(user, twitterScreenName, fileLocation, cal.getTime(), line,
						true));
			}
			reader.close();
		} catch (MalformedURLException e) {
			message = "Invalid File location";
			level = "error";
			log.log(Level.SEVERE, "Invalid URL " + fileLocation);
		} catch (IOException e) {
			message = "Could not fetch contents from " + fileLocation;
			level = "error";
			log.log(Level.SEVERE, "Reading Error from location  " + fileLocation);
		}
		if (message != null) {
			message = "Please select the tweets that you would like to schedule and then click on Add";
		}
		this.constructResponse(statuses, message, level, resp);
	}

	private void processUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String twitterScreenName = req.getParameter(Pages.PARAM_SCREENNAME);
		ServletFileUpload upload = new ServletFileUpload();
		String separator = "\n";
		User user = UserServiceFactory.getUserService().getCurrentUser();
		Date startDate = new Date();
		String message = null, level = "info";
		List<TwitterStatus> statuses = new ArrayList<TwitterStatus>();
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if (!item.isFormField()) {
					int len;
					byte[] buffer = new byte[8192];
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
						byteStream.write(buffer, 0, len);
					}
					String[] statusArray = byteStream.toString().split(separator);
					int increment = 0;
					for (String status : statusArray) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(startDate);
						cal.add(Calendar.MINUTE, increment += DEFAULT_TIME_INCREMENT);
						statuses.add(new TwitterStatus(user, twitterScreenName, item.getName(),
								cal.getTime(), status, true));
					}
					log.log(Level.INFO, "Added " + statuses.size() + "tweets from " + item.getName());
				}
			}
		} catch (FileUploadException e) {
			log.log(Level.SEVERE, "", e);
			e.printStackTrace(resp.getWriter());
			message = "There was a problem in uploading the file";
			level = "error";
		}
		if (message == null) {
			message = "Please select the tweets that you would like to schedule and then click on Add";
		}
		this.constructResponse(statuses, message, level, resp);
	}

	private void constructResponse(List<TwitterStatus> list, String message, String level,
			HttpServletResponse resp) throws IOException {
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, list);
		templateValues.put(Pages.FTLVAR_LEVEL, level);
		templateValues.put(Pages.FTLVAR_MESSAGE, message);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
	}

	private List<TwitterStatus> getTwitterStatus(String screenName, PersistenceManager pm) {
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("twitterScreenName == twitterScreenNameVar && user == userVar");
		query.declareParameters("String twitterScreenNameVar, com.google.appengine.api.users.User userVar");
		query.setOrdering("updatedTime asc");
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(screenName,
				UserServiceFactory.getUserService().getCurrentUser());
		return twitterStatuses;
	}

	private boolean getBoolFromParam(String param, String trueValue) {
		if (param != null && param.equals(trueValue)) {
			return true;
		} else {
			return false;
		}
	}
}

package com.appspot.twitteybot.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
import javax.servlet.http.Cookie;
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

	private static final Logger	log							= Logger.getLogger(StatusManager.class.getName());
	private static final long		serialVersionUID			= 1551252388567429753L;
	private static final int		DEFAULT_TIME_INCREMENT	= 60;

	private static final String	LEVEL_INFO					= "info";
	private static final String	LEVEL_ERROR					= "error";
	private static final String	LEVEL_WARN					= "warn";
	private static final long		PAGE_SIZE					= 30;
	public static final String		DATE_FORMAT					= "MM/dd/yyyy";
	public static final String		TIME_FORMAT					= "hh:mm";

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
		long start = 0;
		long end = PAGE_SIZE;
		try {
			start = Long.parseLong(req.getParameter(Pages.PARAM_START));
		} catch (NumberFormatException e) {
		}
		try {
			end = Long.parseLong(req.getParameter(Pages.PARAM_END));
		} catch (NumberFormatException e) {
		}
		this.constructResponse(this.getTwitterStatus(req.getParameter(Pages.PARAM_SCREENNAME), pm, start, end),
				"Showing " + (end - start) + " tweets", LEVEL_INFO, resp, start, end);
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
		String level = LEVEL_INFO;
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
					Key key = KeyFactory.createKey(TwitterStatus.class.getSimpleName(), Long.parseLong(id.replace(
							",", "")));
					twitterStatus = pm.getObjectById(TwitterStatus.class, key);
					twitterStatuses.add(twitterStatus);
				}
				if (twitterStatus != null && user.getEmail().equals(twitterStatus.getUser().getEmail())) {
					if (!delete) {
						try {
							twitterStatus.setTime(req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i));
						} catch (RuntimeException e) {
							message = "There were errors parsing the time for tweets. Some tweets were not updated.";
							level = LEVEL_WARN;
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
		String level = LEVEL_INFO;

		if (screenName == null || screenName == "") {
			message = "Twitter Screen Name was null and hence, could not add tweets";
			level = LEVEL_ERROR;
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
						level = LEVEL_WARN;
						log.log(Level.WARNING, "Could not add "
								+ req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i) + " as parsing failed");
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
		String level = LEVEL_INFO;
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
				statuses.add(new TwitterStatus(user, twitterScreenName, fileLocation, cal.getTime(), line, true));
			}
			reader.close();
		} catch (MalformedURLException e) {
			message = "Invalid File location";
			level = LEVEL_ERROR;
			log.log(Level.SEVERE, "Invalid URL " + fileLocation);
		} catch (IOException e) {
			message = "Could not fetch contents from " + fileLocation;
			level = LEVEL_ERROR;
			log.log(Level.SEVERE, "Reading Error from location  " + fileLocation);
		}
		if (message != null) {
			message = "Please select the tweets that you would like to schedule and then click on Add";
		}
		this.constructResponse(statuses, message, level, resp);
	}

	private void processUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String twitterScreenName = req.getParameter(Pages.PARAM_SCREENNAME);
		boolean isCSVFile = req.getParameter(Pages.PARAM_CSVFILE) != null ? true : false;
		ServletFileUpload upload = new ServletFileUpload();
		String separator = "\n";
		User user = UserServiceFactory.getUserService().getCurrentUser();
		Date startDate = new Date();
		String message = null, level = LEVEL_INFO;
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
					DateFormat dateFormat = new SimpleDateFormat(StatusManager.DATE_FORMAT);
					DateFormat timeFormat = new SimpleDateFormat(StatusManager.TIME_FORMAT);
					dateFormat.setLenient(true);
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					int browserTimeZoneOffset = this.getBrowserTimeZone(req);
					for (String status : statusArray) {
						if (isCSVFile) {
							String[] parts = status.split(",", 3);
							try {
								Date datePart = dateFormat.parse(parts[0], new ParsePosition(0));
								if (datePart != null) {
									cal.setTime(datePart);
								}
								Date timePart = timeFormat.parse(parts[1], new ParsePosition(0));
								if (timePart != null) {
									Calendar timeCal = Calendar.getInstance();
									timeCal.setTime(timePart);
									timeCal.add(Calendar.MINUTE, browserTimeZoneOffset);
									cal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR));
									cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
								}
								System.out.println(cal.getTime());
								System.out.println(status);
								System.out.println("=========================");
								status = parts[parts.length - 1];
							} catch (ArrayIndexOutOfBoundsException e) {
								message = "Some lines in the file that you uploaded could not be parsed";
								level = LEVEL_WARN;
								log.log(Level.WARNING, "Could not parse line " + status);
							}
						} else {
							cal.add(Calendar.MINUTE, DEFAULT_TIME_INCREMENT);
						}
						statuses.add(new TwitterStatus(user, twitterScreenName, item.getName(), cal.getTime(),
								status, true));
					}
					log.log(Level.INFO, "Added " + statuses.size() + "tweets from " + item.getName());
				}
			}
		} catch (FileUploadException e) {
			log.log(Level.SEVERE, "", e);
			e.printStackTrace(resp.getWriter());
			message = "There was a problem in uploading the file";
			level = LEVEL_ERROR;
		}
		if (message == null) {
			message = "Please select the tweets that you would like to schedule and then click on Add";
		}
		this.constructResponse(statuses, message, level, resp);
	}

	private int getBrowserTimeZone(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		int result = 0;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(Pages.COOKIE_TIMEZONE)) {
				try {
					result = Integer.parseInt(cookie.getValue());
					break;
				} catch (NumberFormatException e) {
					log.log(Level.WARNING, "Could not parse timezeon " + cookie.getValue());
				}
			}
		}
		return result;
	}

	private void constructResponse(List<TwitterStatus> list, String message, String level,
			HttpServletResponse resp, long start, long end) throws IOException {
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, list);
		templateValues.put(Pages.FTLVAR_LEVEL, level);
		templateValues.put(Pages.FTLVAR_START, start);
		templateValues.put(Pages.FTLVAR_END, start + list.size());
		templateValues.put(Pages.FTLVAR_MESSAGE, message);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
	}

	private void constructResponse(List<TwitterStatus> list, String message, String level,
			HttpServletResponse resp) throws IOException {
		this.constructResponse(list, message, level, resp, 0, PAGE_SIZE);
	}

	private List<TwitterStatus> getTwitterStatus(String screenName, PersistenceManager pm, long start, long end) {
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("twitterScreenName == twitterScreenNameVar && user == userVar");
		query.declareParameters("String twitterScreenNameVar, com.google.appengine.api.users.User userVar");
		query.setOrdering("updatedTime asc");
		query.setRange(start, end);
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(screenName,
				UserServiceFactory.getUserService().getCurrentUser());
		return twitterStatuses;
	}

	private List<TwitterStatus> getTwitterStatus(String screenName, PersistenceManager pm) {
		return this.getTwitterStatus(screenName, pm, 0, PAGE_SIZE);
	}

	private boolean getBoolFromParam(String param, String trueValue) {
		if (param != null && param.equals(trueValue)) {
			return true;
		} else {
			return false;
		}
	}
}

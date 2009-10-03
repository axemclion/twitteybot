package com.appspot.twitteybot.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Responsible for parsing a file that is uploaded with the status messages.
 * Parses the file and presents it to the user to allow modification
 */
public class StatusManager extends HttpServlet {

	private static final Logger log = Logger.getLogger(StatusManager.class.getName());
	private static final long serialVersionUID = 1551252388567429753L;
	private static final int DEFAULT_TIME_INCREMENT = 1;

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
		} else if (action.equals(Pages.PARAM_ACTION_UPLOAD)) {
			this.processUpload(req, resp);
		} else if (action.equals(Pages.PARAM_ACTION_ADD)) {
			this.processAdd(req, resp);
		} else if (action.equals(Pages.PARAM_ACTION_DELETE)) {
			this.processDelete(req, resp);
		} else {
			this.processShow(req, resp);
		}
	}

	private void processShow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, this.getTwitterStatus(req
				.getParameter(Pages.PARAM_SCREENNAME), pm));
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
		pm.close();
	}

	private void processDelete(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
	}

	private void processAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int totalItems = Integer.parseInt(req.getParameter(Pages.PARAM_TOTAL_ITEMS));
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String screenName = req.getParameter(Pages.PARAM_SCREENNAME);
		Map<String, Object> templateValues = new HashMap<String, Object>();
		User user = UserServiceFactory.getUserService().getCurrentUser();
		if (screenName == null || screenName == "") {
			templateValues.put(Pages.FTLVAR_TWITTER_STATUS, this.getTwitterStatus(screenName, pm));
			templateValues.put(Pages.FTLVAR_MESSAGE, Messages
					.get("Twitter Screen Name was null and hence, could not add tweets"));
			templateValues.put(Pages.FTLVAR_LEVEL, "error");
			log.log(Level.SEVERE, "ScerenName supplied was null");
		} else {
			List<TwitterStatus> twitterStatuses = new ArrayList<TwitterStatus>();
			SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy, hh:mm:ss a (zzz)");
			List<String> dateParseErrors = new ArrayList<String>();
			for (int i = 0; i <= totalItems; i++) {
				if (this.getBoolFromParam(req.getParameter(Pages.PARAM_STATUS_CANADD + i), "on")) {
					Date updateDate = new Date();
					try {
						updateDate = df.parse(req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i));
					} catch (ParseException e) {
						dateParseErrors.add(req.getParameter(Pages.PARAM_STATUS_UPDATE_DATE + i));
					}
					twitterStatuses.add(new TwitterStatus(user, screenName, req
							.getParameter(Pages.PARAM_STATUS_SOURCE + i), updateDate, req
							.getParameter(Pages.PARAM_STATUS_STATUS + i), this.getBoolFromParam(req
							.getParameter(Pages.PARAM_STATUS_CAN_DELETE + i), "on")));

				}
			}
			pm.makePersistentAll(twitterStatuses);
			templateValues.put(Pages.FTLVAR_TWITTER_STATUS, this.getTwitterStatus(screenName, pm));
			templateValues.put(Pages.FTLVAR_LEVEL, "info");
			templateValues.put(Pages.FTLVAR_MESSAGE, Messages.get("All messages successfully added"));
		}
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
		pm.close();
	}

	private List<TwitterStatus> getTwitterStatus(String screenName, PersistenceManager pm) {
		Query query = pm.newQuery(TwitterStatus.class);
		query.setFilter("twitterScreenName == twitterScreenNameVar && user == userVar");
		query.declareParameters("String twitterScreenNameVar, com.google.appengine.api.users.User userVar");
		@SuppressWarnings("unchecked")
		List<TwitterStatus> twitterStatuses = (List<TwitterStatus>) query.execute(screenName,
				UserServiceFactory.getUserService().getCurrentUser());
		return twitterStatuses;
	}

	private void processUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String twitterScreenName = req.getParameter(Pages.PARAM_SCREENNAME);
		ServletFileUpload upload = new ServletFileUpload();
		String separator = "\n";
		Date startDate = new Date();
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
						statuses.add(new TwitterStatus(UserServiceFactory.getUserService().getCurrentUser(),
								twitterScreenName, item.getName(), cal.getTime(), status, true));
					}
				}
			}
		} catch (FileUploadException e) {
			log.log(Level.SEVERE, "", e);
			e.printStackTrace(resp.getWriter());
		}
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, statuses);
		templateValues.put(Pages.PARAM_ACTION, Pages.PARAM_ACTION_ADD);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());
	}

	private boolean getBoolFromParam(String param, String trueValue) {
		if (param != null && param.equals(trueValue)) {
			return true;
		} else {
			return false;
		}
	}
}

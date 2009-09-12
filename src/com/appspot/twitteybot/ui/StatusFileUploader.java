package com.appspot.twitteybot.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.appspot.twitteybot.datastore.TwitterStatus;

/**
 * Responsible for parsing a file that is uploaded with the status messages.
 * Parses the file and presents it to the user to allow modification
 */
public class StatusFileUploader extends HttpServlet {

	private static final Logger log = Logger.getLogger(StatusFileUploader.class.getName());
	private static final long serialVersionUID = 1551252388567429753L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} else if (action.equals(Pages.PARAM_ACTION_ADD)) {
			this.processUpload(req, resp);
		} else if (action.equals(Pages.PARAM_ACTION_UPDATE)) {

		}
	}

	private void processUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String twitterScreenName = req.getParameter(Pages.PARAM_SCREENNAME);
		ServletFileUpload upload = new ServletFileUpload();
		String separator = "\n";
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
					for (String status : statusArray) {
						TwitterStatus twitterStatus = new TwitterStatus();
						twitterStatus.setSource(item.getName());
						twitterStatus.setStatus(status);
						twitterStatus.setUpdatedTime(new Date());
						twitterStatus.setTwitterScreenName(twitterScreenName);
						statuses.add(twitterStatus);
					}
				}
			}
		} catch (FileUploadException e) {
			log.log(Level.SEVERE, "", e);
			e.printStackTrace(resp.getWriter());
		}
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(Pages.FTLVAR_TWITTER_STATUS, statuses);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_STATUSPAGE, resp.getWriter());

	}
}

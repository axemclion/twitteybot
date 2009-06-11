package com.appspot.twitteybot.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.helper.FeedConfigHelper;
import com.appspot.twitteybot.datastore.helper.StatusHelper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Responsible for parsing a file that is uploaded with the status messages.
 * Parses the file and presents it to the user to allow modification
 */
public class StatusFileUploader extends HttpServlet {

    private static final Logger log = Logger.getLogger(StatusFileUploader.class.getName());
    private static final long serialVersionUID = 1551252388567429753L;

    private static final String PARAM_SEPARATOR = "seperator";
    private static final String PARAM_TWITTER = "twitter";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user == null) {
	    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not a registered user to perform this operation");
	    return;
	}

	ServletFileUpload upload = new ServletFileUpload();
	String separator = this.getSeparator(req.getParameter(PARAM_SEPARATOR));
	String twitterName = req.getParameter(PARAM_TWITTER);

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
		    StatusHelper helper = new StatusHelper(item.getName());
		    helper.addStatus(Arrays.asList(byteStream.toString().split(separator)));
		    helper.commit();

		    FeedConfigHelper feedConfigHelper = new FeedConfigHelper(user, twitterName);
		    feedConfigHelper.setFeedConfig(item.getName(), 0);
		    feedConfigHelper.commit();
		}
	    }
	} catch (FileUploadException e) {
	    log.log(Level.SEVERE, "", e);
	    e.printStackTrace(resp.getWriter());
	}
    }

    private String getSeparator(String separator) {
	if (separator == null) {
	    separator = "\n";
	}
	Map<String, String> substituteMap = new HashMap<String, String>();
	substituteMap.put("blankLine", "\n\n");
	substituteMap.put("nextLine", "\n");
	if (substituteMap.containsKey(separator)) {
	    return substituteMap.get(separator);
	} else {
	    return separator;
	}
    }
}

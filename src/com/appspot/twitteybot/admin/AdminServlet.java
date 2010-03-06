package com.appspot.twitteybot.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.ApplicationProperty;
import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.ui.FreeMarkerConfiguration;
import com.appspot.twitteybot.ui.Pages;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 6405416403272879573L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.wrieAdminForm(resp.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action != null && action.equalsIgnoreCase(Pages.PARAM_ACTION_ADD)) {
			ApplicationProperty.write(ApplicationProperty.CONSUMER_KEY, req
					.getParameter(ApplicationProperty.CONSUMER_KEY));
			ApplicationProperty.write(ApplicationProperty.CONSUMER_SECRET, req
					.getParameter(ApplicationProperty.CONSUMER_SECRET));
			resp.getWriter().write("Properties added");
		} else {
			this.wrieAdminForm(resp.getWriter());
		}
	}

	private void wrieAdminForm(PrintWriter w) {
		String consumerKey = ApplicationProperty.read(ApplicationProperty.CONSUMER_KEY);
		String consumerSecret = ApplicationProperty.read(ApplicationProperty.CONSUMER_SECRET);
		Map<String, Object> templateValues = new HashMap<String, Object>();
		templateValues.put(ApplicationProperty.CONSUMER_KEY, consumerKey);
		templateValues.put(ApplicationProperty.CONSUMER_SECRET, consumerSecret);
		FreeMarkerConfiguration.writeResponse(templateValues, Pages.TEMPLATE_ADMINPAGE, w);

	}
}

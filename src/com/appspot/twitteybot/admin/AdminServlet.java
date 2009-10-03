package com.appspot.twitteybot.admin;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.ApplicationProperty;
import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.ui.Pages;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 6405416403272879573L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		String action = req.getParameter(Pages.PARAM_ACTION);
		if (action == null) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} else if (action.equalsIgnoreCase(Pages.PARAM_ACTION_ADD)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			ApplicationProperty prop = new ApplicationProperty(req.getParameter(Pages.PARAM_KEY), req
					.getParameter(Pages.PARAM_VALUE));
			pm.makePersistent(prop);
			pm.close();
		} else {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}
}
package com.appspot.twitteybot.cron;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusDeleteCron extends HttpServlet {

	private static final long serialVersionUID = -1578502604462452971L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		// TODO Delete status messages that have been posted
	}
}

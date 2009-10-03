package com.appspot.twitteybot;

import java.io.IOException;
import java.io.Writer;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.twitteybot.datastore.PMF;
import com.appspot.twitteybot.datastore.TwitterStatus;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 2205142583490201618L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Writer out = resp.getWriter();
		Key key = KeyFactory.createKey(TwitterStatus.class.getSimpleName(), 2);
		TwitterStatus status = pm.getObjectById(TwitterStatus.class, key);
		status.setStatus("AXE");
		out.write(status.toString());
		pm.close();
	}
}

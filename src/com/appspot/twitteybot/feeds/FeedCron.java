package com.appspot.twitteybot.feeds;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FeedCron
    extends HttpServlet {
    private static final long serialVersionUID = 3266332204576717233L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {
        
        //TODO Call feed reader to save feeds
    }
}

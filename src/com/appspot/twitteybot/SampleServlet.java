package com.appspot.twitteybot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleServlet extends HttpServlet {

    private static final long serialVersionUID = -7936711444530844272L;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    /*
     * private void setData() { UserService userService =
     * UserServiceFactory.getUserService(); User user =
     * userService.getCurrentUser();
     * 
     * PersistenceManager pm = PMF.get().getPersistenceManager();
     * 
     * FeedConfiguration url1 = new
     * FeedConfiguration("http://rss.news.yahoo.com/rss/topstories", 10);
     * FeedConfiguration url2 = new
     * FeedConfiguration("http://rss.news.yahoo.com/rss/highestrated", 3);
     * 
     * List<FeedConfiguration> urls = new ArrayList<FeedConfiguration>();
     * urls.add(url1); urls.add(url2); TwitterAccount account = new
     * TwitterAccount(); account.setFeedUrls(urls);
     * account.setUserName("twitteybot"); account.setPassword("Welcome1");
     * account.setTwitterInterval(10L);
     * 
     * List<TwitterAccount> accounts = new ArrayList<TwitterAccount>();
     * accounts.add(account);
     * 
     * Settings testSettings = new Settings(); testSettings.setDateCreated(new
     * Date()); testSettings.setUser(user);
     * testSettings.setTwitterAccounts(accounts);
     * 
     * pm.makePersistent(testSettings); }
     */
}

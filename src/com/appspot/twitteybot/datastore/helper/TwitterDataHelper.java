package com.appspot.twitteybot.datastore.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import com.appspot.twitteybot.datastore.FeedConfiguration;
import com.appspot.twitteybot.datastore.TwitterAccount;
import com.appspot.twitteybot.datastore.UserConfig;
import com.google.appengine.api.users.User;

/**
 * DAO helper class, tries to abstract the way the data is obtained or
 * structured at the data store level
 */
public class TwitterDataHelper {

    private static final Logger log = Logger.getLogger(TwitterDataHelper.class.getName());

    private UserConfigDataHelper userConfigHelper;

    /**
     * Public constructor that also performs the lookup of the user from the
     * data store
     * 
     * @param user
     *            is set for all operations on the helper object
     */
    public TwitterDataHelper(User user) {
	this.userConfigHelper = new UserConfigDataHelper(user);
    }

    public TwitterDataHelper(UserConfigDataHelper userConfigHelper) {
	this.userConfigHelper = userConfigHelper;
    }

    /**
     * @return List of all Twitter accounts associated with this user
     */
    public List<TwitterAccount> getAllTwitterAccounts() {
	UserConfig config = this.userConfigHelper.getUserConfig();
	if (config == null) {
	    return new ArrayList<TwitterAccount>();
	} else {
	    return config.getTwitterAccounts();
	}

    }

    /**
     * Gets a specific twitter name
     * 
     * @param twitterName
     *            search for this specific twitter user name
     * @return the TwitterAccount object that has the specified twitter user
     *         name
     */
    public TwitterAccount getTwitterAccount(final String twitterName) {
	List<TwitterAccount> accounts = this.getAllTwitterAccounts();
	TwitterAccount result = null;
	for (TwitterAccount account : accounts) {
	    if (account.getTwitterName().equals(twitterName)) {
		result = account;
		break;
	    }
	}
	return result;
    }

    /**
     * Gets all feeds for a specific twitter name
     * 
     * @param twitterName
     *            Twitter user name
     * @return All feedConfigurations for twitter username
     */
    public List<FeedConfiguration> getAllFeedConfigurations(final String twitterName) {
	return this.getTwitterAccount(twitterName).getFeedUrls();
    }

    /**
     * Gets a specific feed that matches the specified URL
     * 
     * @param twitterName
     *            user name for twitter
     * @param feedUrl
     *            URL of the feed being searched for
     * @return
     */
    public FeedConfiguration getFeedConfiguration(final String twitterName, String feedUrl) {
	FeedConfiguration result = null;
	for (FeedConfiguration feed : this.getAllFeedConfigurations(twitterName)) {
	    if (feed.getFeedUrl().equals(feedUrl)) {
		result = feed;
		break;
	    }
	}
	return result;
    }

    /**
     * Convienence method that Dumps the list of all feeds for all users
     * 
     * @return list of all feeds for all accounts, for all feeds
     */
    public static List<FeedConfiguration> dumpAllFeeds() {
	return null;
    }

    /**
     * Saves all modification made to the user or child objects. Once committed,
     * this object should not be modified for furthur use
     */
    public void commit() {
	this.userConfigHelper.commit();
    }

    /**
     * Creates a new twitter account. If a twitter account with the name exists,
     * it updates it.
     * 
     * @param username
     * @param password
     * @param interval
     */
    public boolean setTwitterAccount(String username, String password, int interval) {
	TwitterAccount account = this.getTwitterAccount(username);
	if (account == null) {
	    account = new TwitterAccount();
	    account.setTwitterName(username);
	    log.log(Level.FINE, "Created ");
	    this.userConfigHelper.getUserConfig().getTwitterAccounts().add(account);
	}
	account.setPassword(password);
	account.setTwitterInterval(interval);
	return true;
    }

    /**
     * Deletes the twitter account name from store
     * 
     * @param username
     * @return
     */
    public boolean deleteAccount(String username) {
	PersistenceManager pm = PMF.get().getPersistenceManager();
	TwitterAccount acc = this.getTwitterAccount(username);
	acc = pm.getObjectById(TwitterAccount.class, acc.getKey());
	pm.deletePersistent(acc);
	return true;
    }

    public static List<TwitterAccount> dumpAllAccounts() {
	List<TwitterAccount> result = new ArrayList<TwitterAccount>();
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Extent<UserConfig> userConfigs = pm.getExtent(UserConfig.class);
	for (UserConfig config : userConfigs) {
	    for (TwitterAccount twitterAccount : config.getTwitterAccounts()) {
		result.add(twitterAccount);
	    }
	}
	return result;
    }
}
package com.appspot.twitteybot.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.User;

/**
 * DAO helper class, tries to abstract the way the data is obtained or
 * structured at the data store level
 */
public class DataStoreHelper {

    private static final Logger log = Logger.getLogger(DataStoreHelper.class.getName());

    private User user;
    private UserConfig config;
    private PersistenceManager pm;

    /**
     * Public constructor that also performs the lookup of the user from the
     * data store
     * 
     * @param user
     *            is set for all operations on the helper object
     */
    public DataStoreHelper(User user) {
	this.pm = PMF.get().getPersistenceManager();
	this.user = user;
	this.config = this.populateUserConfigFromStore();
    }

    @SuppressWarnings("unchecked")
    private UserConfig populateUserConfigFromStore() {
	Query query = pm.newQuery(UserConfig.class);
	query.setFilter("user == userName");
	query.declareParameters("com.google.appengine.api.users.User userName");
	List<UserConfig> users = (List<UserConfig>) query.execute(user);
	UserConfig result = null;
	if (users != null && users.size() > 0) {
	    result = users.get(0);
	} else {
	    log.log(Level.INFO, "User not found, creating one in the data store", user.getNickname());
	    result = new UserConfig();
	    result.setUser(user);
	    result.setTwitterAccounts(new ArrayList<TwitterAccount>());
	    this.pm.makePersistent(result);
	}
	return result;
    }

    /**
     * Gets the user configuration
     * 
     * @return the UserConfig object for the user, obtained from the datastore
     */
    public UserConfig getUserConfig() {
	return this.config;
    }

    /**
     * @return List of all Twitter accounts associated with this user
     */
    public List<TwitterAccount> getAllTwitterAccounts() {
	UserConfig userConfig = this.getUserConfig();
	return userConfig.getTwitterAccounts();
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
	    if (account.getUserName().equals(twitterName)) {
		result = account;
		break;
	    }
	}
	log.log(Level.FINER, user.getNickname(), result);
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
	this.pm.close();
    }
}
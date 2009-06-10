package com.appspot.twitteybot.datastore.helper;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.twitteybot.datastore.UserConfig;
import com.google.appengine.api.users.User;

/**
 * A helper class that abstracts out the UserConfig entity.
 */
public class UserConfigDataHelper {

    private UserConfig config;
    private PersistenceManager pm;

    private static final Logger log = Logger.getLogger(UserConfigDataHelper.class.getName());

    public UserConfigDataHelper(User user) {
	this.pm = PMF.get().getPersistenceManager();
	this.config = this.populateUserConfigFromStore(user);
    }

    @SuppressWarnings("unchecked")
    private UserConfig populateUserConfigFromStore(User user) {
	Query query = pm.newQuery(UserConfig.class);
	query.setFilter("user == userName");
	query.declareParameters("com.google.appengine.api.users.User userName");
	List<UserConfig> users = (List<UserConfig>) query.execute(user);
	UserConfig result = null;
	if (users != null && users.size() > 0) {
	    result = users.get(0);
	}
	return result;
    }

    /**
     * Creates a new user with the Id of the user who has logged in.
     * 
     * @param user
     */
    public void createNewUser(User user) {
	log.log(Level.INFO, "User not found, creating one in the data store", user.getNickname());
	UserConfig result = new UserConfig();
	result.setUser(user);
	this.pm.makePersistent(result);
	this.config = result;
    }

    /**
     * Gets the user configuration
     * 
     * @return the UserConfig object for the user, obtained from the datastore
     */
    public UserConfig getUserConfig() {
	return this.config;
    }

    public void commit() {
	this.pm.close();
    }
}

package com.appspot.twitteybot.datastore;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

/**
 * Class holding the configuration settings on individual twitter accounts
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserConfig implements Serializable {

    private static final long serialVersionUID = 2730123362861801431L;

    @SuppressWarnings("unused")
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private List<TwitterAccount> twitterAccounts;

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public List<TwitterAccount> getTwitterAccounts() {
	return twitterAccounts;
    }

    public void setTwitterAccounts(List<TwitterAccount> twitterAccounts) {
	this.twitterAccounts = twitterAccounts;
    }
}

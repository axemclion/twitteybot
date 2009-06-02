package com.appspot.twitteybot.datastore;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

/**
 * Class that holds the setting of a specific user
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Settings {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private User user;

    @Persistent
    private List<TwitterAccount> twitterAccounts;

    @Persistent
    private Date dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}

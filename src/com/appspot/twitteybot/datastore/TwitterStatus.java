package com.appspot.twitteybot.datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterStatus implements Serializable {

	private static final long serialVersionUID = -9122716449061595598L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String twitterScreenName;
	@Persistent
	private User user;
	@Persistent
	private Date updatedTime;
	@Persistent
	private String status;
	@Persistent
	private String source;
	@Persistent
	private State state;
	@Persistent
	private boolean canDelete;

	enum State {
		SCHEDULED, TO_DELETE, UPDATED
	}

	public TwitterStatus(User user, String twitterScreenName, String source, Date updateTime, String status,
			boolean canDelete) {
		this.user = user;
		this.twitterScreenName = twitterScreenName;
		this.source = source;
		this.state = State.SCHEDULED;
		this.updatedTime = updateTime;
		this.status = status;
		this.canDelete = canDelete;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTwitterScreenName() {
		return twitterScreenName;
	}

	public void setTwitterScreenName(String twitterScreenName) {
		this.twitterScreenName = twitterScreenName;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

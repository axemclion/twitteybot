package com.appspot.twitteybot.datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitterStatus implements Serializable {

	private static final long serialVersionUID = -9122716449061595598L;
	public static final String DATE_FORMAT_STRING = "yyyy-mm-dd,hh:mm:ss a (Z)";

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String encodedKey;

	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.pk-id", value = "true")
	private Long keyId;

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

	public enum State {
		SCHEDULED, TO_DELETE, QUEUED
	}

	public TwitterStatus(User user, String twitterScreenName, String source, Date updateTime, String status,
			boolean canDelete) {
		this.user = user;
		this.twitterScreenName = twitterScreenName;
		this.source = source;
		this.state = State.SCHEDULED;
		this.updatedTime = updateTime;
		this.status = status.substring(0, status.length() > 140 ? 140 : (status.length() == 0) ? 0 : status
				.length() - 1);
		this.canDelete = canDelete;
	}

	public TwitterStatus(User user, String twitterScreenName, String source, String updateTime,
			String status, boolean canDelete) {
		this.user = user;
		this.twitterScreenName = twitterScreenName;
		this.source = source;
		this.state = State.SCHEDULED;
		this.setTime(updateTime);
		this.status = status.substring(0, status.length() > 140 ? 140 : (status.length() == 0) ? 0 : status
				.length() - 1);
		this.canDelete = canDelete;
	}

	public String getTwitterScreenName() {
		return twitterScreenName;
	}

	public void setTwitterScreenName(String twitterScreenName) {
		this.twitterScreenName = twitterScreenName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public String getTime() {
		if (this.updatedTime == null) {
			return null;
		} else {
			return Long.toString(this.updatedTime.getTime());
		}
	}

	public void setTime(String time) {
		Date date = new Date();
		date.setTime(Long.parseLong(time));
		this.setUpdatedTime(date);
	}

	@Override
	public String toString() {
		return "TwitterStatus [canDelete=" + canDelete + ", encodedKey=" + encodedKey + ", keyId=" + keyId
				+ ", source=" + source + ", state=" + state + ", status=" + status + ", twitterScreenName="
				+ twitterScreenName + ", updatedTime=" + updatedTime + ", user=" + user + "]";
	}

}

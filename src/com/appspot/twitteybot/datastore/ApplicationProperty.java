package com.appspot.twitteybot.datastore;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ApplicationProperty {

	public static final String CONSUMER_KEY = "consumer_key";
	public static final String CONSUMER_SECRET = "consumer_secret";

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String key;
	@Persistent
	private String value;

	public ApplicationProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

}

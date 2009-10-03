package com.appspot.twitteybot.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the messages class. Will be used in the future for localization
 * 
 * @author axe
 * 
 */
public class Messages {
	private static Map<String, String> messages;

	public static String get(String key) {
		if (messages == null) {
			initializeMessages();
		}
		return key;
	}

	public static void initializeMessages() {
		messages = new HashMap<String, String>();
	}
}

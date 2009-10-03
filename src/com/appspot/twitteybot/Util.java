package com.appspot.twitteybot;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.twitteybot.datastore.ApplicationProperty;
import com.appspot.twitteybot.datastore.PMF;

public class Util {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Util.class.getName());
	private static final String APPLICATION_PROPERTIES = "application_properties";

	@SuppressWarnings("unchecked")
	public static String getApplicationProperty(String key) {
		CacheManager cacheManager = CacheManager.getInstance();
		Cache cache = cacheManager.getCache(APPLICATION_PROPERTIES);
		if (cache == null) {
			log.info("Cache not found, creating a new cache");
			try {
				cache = cacheManager.getCacheFactory().createCache(Collections.emptyMap());
			} catch (CacheException e) {
				throw new RuntimeException("Could not create Cache", e);
			}
			cacheManager.registerCache(APPLICATION_PROPERTIES, cache);
		}

		Object obj = cache.get(key);
		String result = null;
		if (obj instanceof String) {
			result = (String) obj;
		}
		if (result != null) {
			return result;
		}

		log.info(key + " not found in cache. Putting it in now");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(ApplicationProperty.class);
		query.setFilter("key == keyVar");
		query.declareParameters("String keyVar");
		List<ApplicationProperty> properties = (List<ApplicationProperty>) query
				.execute(key);
		if (properties.size() == 0) {
			log.log(Level.SEVERE, "Consumer Key not set in the database");
			throw new RuntimeException(key + " not found in database. Please configure it first");
		}
		query.closeAll();
		result = ((ApplicationProperty) properties.get(0)).getValue();
		pm.close();
		cache.put(key, result);
		return result;
	}
}

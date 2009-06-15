package com.appspot.twitteybot.cron;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

import com.google.appengine.api.users.User;

/**
 * This is the scheduler abstract class that is extended by individual Cron
 * tasks. Also handles the MemCache stuff
 */
public abstract class Scheduler {

    private static final Logger log = Logger.getLogger(Scheduler.class.getName());

    private static final int CRON_INTERVAL = 2; // indicating 2 minutes between
    // private static final int MAX_JOBS = 3; // maximum jobs per cycle

    private static Scheduler instance;

    private String cacheId;
    protected String cacheKey;

    protected List<?> jobList;
    private Map<Object, Date> scheduledJobs;

    private User user;

    public Scheduler() {
	this.cacheId = "CACHE";
    }

    /**
     * This is the method that is called every time the cron is triggered. The
     * arguments passed to this method vary depending on the time called.
     */
    protected abstract void run(List<?> jobs);

    /**
     * Gets the interval when the job should be called next
     * 
     * @return
     */
    protected abstract int getUpdateInterval(Object job);

    @SuppressWarnings("unchecked")
    protected final Map<Object, Date> getJobs() {
	return (Map<Object, Date>) this.getCache().get(this.cacheKey);
    }

    @SuppressWarnings("unchecked")
    protected final void putJob(Map<Object, Date> item) {
	this.getCache().put(this.cacheKey, item);
    }

    private Cache getCache() {
	Cache cache = CacheManager.getInstance().getCache(this.cacheId);
	if (cache == null) {
	    try {
		cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
		CacheManager.getInstance().registerCache(this.cacheId, cache);
	    } catch (CacheException e) {
		log.log(Level.SEVERE, "Could not create cache", e);
	    }
	}
	return cache;
    }

    /**
     * Does the job of inserting the jobs as a Hash Map into the Cache
     */
    protected final void makeSchedules() {
	if (this.scheduledJobs == null) {
	    this.scheduledJobs = new HashMap<Object, Date>();
	}

	int counter = 0;
	for (Object job : this.jobList) {
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, counter + this.getUpdateInterval(job));
	    this.scheduledJobs.put(job, cal.getTime());
	    counter += Scheduler.CRON_INTERVAL;
	}
	this.putJob(this.scheduledJobs);
    }

    /**
     * Called by the Servlet when Google App Engine triggers the CRON job. The
     * method looks at all the jobs in the job table and determines the ones
     * that have to be passed to the run method. It updates the nextTime for
     * such job and calls the run method implemented by the sub classes
     */
    public final void trigger() {
	if (this.scheduledJobs == null) {
	    this.scheduledJobs = this.getJobs();
	}
	List<Object> jobsToRun = new ArrayList<Object>();
	for (Entry<Object, Date> entry : this.scheduledJobs.entrySet()) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(entry.getValue());
	    if (cal.before(Calendar.getInstance())) {
		jobsToRun.add(entry.getKey());
		cal.add(Calendar.MINUTE, this.getUpdateInterval(entry.getKey()));
		this.scheduledJobs.put(entry.getKey(), cal.getTime());
	    }
	}
	this.run(jobsToRun);
	this.putJob(this.scheduledJobs);
    }

    /**
     * Gets a singleton instance of the class the implements the abstract
     * Scheduler class
     * 
     * @param impl
     *            name of the class that implements the Scheduler
     * @return instance of the class that implements scheduler
     */
    public static Scheduler getInstance(Class<?> impl) {
	if (Scheduler.instance == null) {
	    log.log(Level.INFO, "Instance not found, creating it for the first time");
	    try {
		Scheduler.instance = (Scheduler) impl.newInstance();
	    } catch (InstantiationException e) {
		log.log(Level.SEVERE, "Could not load class", e);
	    } catch (IllegalAccessException e) {
		log.log(Level.SEVERE, "Could not load class", e);
	    }
	    Scheduler.instance.makeSchedules();
	}
	return Scheduler.instance;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public User getUser() {
	return this.user;
    }
}

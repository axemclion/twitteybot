package com.appspot.twitteybot;

import java.io.IOException;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleServlet
    extends HttpServlet {

    private static final String CACHE_NAME = "sampleCacheName";
    private static final String KEY_NAME = "cacheKeyName";

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
        IOException {

        try {
            CacheManager cacheManager = CacheManager.getInstance();
            Cache cache = cacheManager.getCache(SampleServlet.CACHE_NAME);

            if (cache == null) {
                cache = cacheManager.getCacheFactory().createCache(Collections.emptyMap());
                cache.put(SampleServlet.KEY_NAME, "axe");
                cacheManager.registerCache(SampleServlet.CACHE_NAME, cache);
                resp.getWriter().write("Writing to cache");
            } else {
                resp.getWriter().write((String)cache.get(SampleServlet.KEY_NAME));
            }

        } catch (CacheException e) {
            e.printStackTrace();
        }
    }
}

package com.appspot.twitteybot;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class PersistanceFactory {

    private static final PersistenceManagerFactory instance = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");

    private PersistanceFactory() {

    }

    public static PersistenceManager getManager() {
        return instance.getPersistenceManager();
    }

}

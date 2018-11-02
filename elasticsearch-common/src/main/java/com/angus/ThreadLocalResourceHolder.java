package com.angus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class ThreadLocalResourceHolder {
    private static final Log log = LogFactory.getLog(ThreadLocalResourceHolder.class);
    private static final ThreadLocal<Map<Object, Object>> threadLocalResources = new ThreadLocal();

    public ThreadLocalResourceHolder() {
    }

    public static Map<Object, Object> getThreadMap() {
        Map<Object, Object> threadMap = (Map)threadLocalResources.get();
        if (threadMap == null) {
            threadMap = new HashMap();
            threadLocalResources.set(threadMap);
        }

        return (Map)threadMap;
    }

    public static Object getProperty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        } else {
            Map<Object, Object> queryMap = getThreadMap();
            return queryMap.get(key);
        }
    }

    public static void bindProperty(Object key, Object target) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        } else {
            if (getProperty(key) != null) {
                ;
            }

            Map<Object, Object> propertiesMap = getThreadMap();
            propertiesMap.put(key, target);
            if (log.isDebugEnabled()) {
                log.debug("Bound Object [" + target + "] to thread [" + Thread.currentThread().getName() + "]");
            }

        }
    }

    public static void unbindProperty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        } else {
            Map<Object, Object> propertiesMap = getThreadMap();
            if (!propertiesMap.containsKey(key) && log.isDebugEnabled()) {
                log.debug("Removed value [" + key + "] from thread [" + Thread.currentThread().getName() + "]");
            }

            propertiesMap.remove(key);
            if (log.isDebugEnabled()) {
                log.debug("Removed value [" + key + "] from thread [" + Thread.currentThread().getName() + "]");
            }

        }
    }
}

package com.lmxdawn.api.admin.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadVariable {

    private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    public static void clearThreadVariable() {
        threadLocal.remove();
    }

    private static Map createMap(String key, Object obj) {
        Map map = (Map) threadLocal.get();
        if (map == null) {
            map = new HashMap();
        }
        map.put(key, obj);
        return map;
    }

    public static void setUserId(Long userId) {
        Map map = (Map) createMap("CURRENT_USER_ID", userId);
        threadLocal.set(map);
    }

    public static Long getUserId() {
        Map map = (Map) threadLocal.get();
        if (map != null) {
            return (Long) map.get("CURRENT_USER_ID");
        }
        return null;
    }

}

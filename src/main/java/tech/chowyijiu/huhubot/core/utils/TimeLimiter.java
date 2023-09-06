package tech.chowyijiu.huhubot.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flless
 * @date 6/9/2023
 */
public class TimeLimiter {

    private static final Map<String, Long> map = new HashMap<>();

    public static boolean limiting(String key, int milliseconds) {
        long now = System.currentTimeMillis();
        Long last = map.getOrDefault(key, 0L);
        if (now - milliseconds * 1000L < last) return true;
        map.put(key, now);
        return false;
    }
}

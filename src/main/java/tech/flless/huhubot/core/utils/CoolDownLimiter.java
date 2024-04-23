package tech.flless.huhubot.core.utils;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flless
 * @date 6/9/2023
 */
public class CoolDownLimiter {

    private static final Map<String, Long> map = new HashMap<>();

    public static boolean check(String key, int seconds) {
        long now = System.currentTimeMillis();
        Long last = map.getOrDefault(key, 0L);
        if (now - seconds * 1000L < last) return true;
        map.put(key, now);
        return false;
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void clear() {
        map.clear();
    }
}

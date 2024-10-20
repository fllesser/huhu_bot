package com.github.huhubot.plugins.ai.reecho;

import com.github.huhubot.plugins.ai.reecho.entity.resp.RoleList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReechoUtil {

    //name:id
    private static final Map<String, String> RoleMap = new ConcurrentHashMap<>();

    public static boolean contains(String name) {
        return RoleMap.containsKey(name);
    }

    public static void update(RoleList roleList) {
        RoleMap.clear();
        roleList.getData().forEach(role -> RoleMap.put(role.getName(), role.getId()));
    }

    public static String get(String key) {
        return RoleMap.get(key);
    }

    public static boolean isEmpty() {
        return RoleMap.isEmpty();
    }

    public static String randId() {
        List<String> ids = RoleMap.values().stream().toList();
        return ids.get((int) (Math.random() * ids.size()));
    }


}

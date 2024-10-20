package com.github.huhubot.plugins.ai.reecho;

import com.github.huhubot.plugins.ai.reecho.entity.resp.RoleList;
import com.github.huhubot.utils.IocUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReechoUtil {

    //name:id
    private static final Map<String, String> roleMap = new ConcurrentHashMap<>();

    static {
        ReechoClient reechoClient = IocUtil.getBean(ReechoClient.class);
        reechoClient.getVoiceList().getData().forEach(role -> roleMap.put(role.getName(), role.getId()));
    }

    public static boolean contains(String name) {
        return roleMap.containsKey(name);
    }

    public static void update(RoleList roleList) {
        roleMap.clear();
        roleList.getData().forEach(role -> roleMap.put(role.getName(), role.getId()));
    }

    public static String get(String key) {
        return roleMap.get(key);
    }

    public static boolean isEmpty() {
        return roleMap.isEmpty();
    }

    public static String randId() {
        List<String> ids = roleMap.values().stream().toList();
        return ids.get((int) (Math.random() * ids.size()));
    }


}

package com.github.huhubot.adapters.onebot.v11.bot;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FLLess7
 * @date 17/9/2023
 */
public class BotContainer {

    private static final Map<Long, Bot> BOT_MAP = new ConcurrentHashMap<>(1);

    protected static void addBot(Long userId, WebSocketSession session) {
        BOT_MAP.put(userId, new Bot(userId, session));
    }


    protected static int getConnections() {
        return BOT_MAP.size();
    }

    protected static void removeBot(WebSocketSession session) {
        //对map.values()的更改会作用于map
        BOT_MAP.values().removeIf(bot -> bot.getSession() == session);
    }

    public static Bot getBot(Long selfId) {
        return BOT_MAP.get(selfId);
    }

    public static List<Bot> getBots() {
        //values和map是绑定的, 防止用户对其更改
        return BOT_MAP.values().stream().toList();
    }


}

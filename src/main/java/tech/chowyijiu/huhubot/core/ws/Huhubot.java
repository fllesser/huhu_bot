package tech.chowyijiu.huhubot.core.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.chowyijiu.huhubot.core.constant.ANSI;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.event.meta.MetaEvent;
import tech.chowyijiu.huhubot.core.thread.ProcessEventTask;

import java.util.*;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Slf4j
public class Huhubot extends TextWebSocketHandler {

    private static final Map<Long, Bot> BOT_MAP = new HashMap<>(1);

    private static void addBot(Long userId, WebSocketSession session) {
        BOT_MAP.put(userId, new Bot(userId, session));
    }

    public static Bot getBot(Long selfId) {
        return BOT_MAP.get(selfId);
    }

    public static int getConnections() {
        return BOT_MAP.size();
    }

    /**
     * 获取所有Bot
     *
     * @return List<Bot>
     */
    public static List<Bot> getBots() {
        //values和map是绑定的, 防止用户对其更改
        return BOT_MAP.values().stream().toList();
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        log.info("{}GOCQ CONNECT SUCCESS, REMOTE[{}], CLIENT_NUM[{}]{}", ANSI.YELLOW,
                session.getRemoteAddress(), getConnections() + 1, ANSI.RESET);
    }

    @Override
    public void handleTextMessage(final @NotNull WebSocketSession session, final TextMessage message) {
        final String json = message.getPayload();
        try {
            final Event event = Event.build(JSONObject.parseObject(json));
            if (event == null) return;
            if (event instanceof MetaEvent metaEvent) {
                if (metaEvent.heartbeat()) return;//心跳忽略
                else if (metaEvent.connect()) {
                    //刚连接成功时，gocq会发一条消息给bot, 添加bot对象到bots中
                    Huhubot.addBot(event.getSelfId(), session);
                    log.info("{}RECEIVED GOCQ CLIENT[{}] CONNECTION SUCCESS MESSAGE{}", ANSI.YELLOW,
                            metaEvent.getSelfId(), ANSI.RESET);
                    return;
                }
            }
            //测试
            //if (bots.isEmpty() && event.getSelfId() == 888888L) addBot(event.getSelfId(), session);
            ProcessEventTask.execute(BOT_MAP.get(event.getSelfId()), event);
        } catch (Exception e) {
            log.error("handleTextMessage exception", e);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("{}connect exception sessionId[{}], exception[{}]{}",
                ANSI.YELLOW, session.getId(), exception.getMessage(), ANSI.RESET);
        removeBot(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("{}connect close, sessionId:{},{}{}", ANSI.YELLOW,
                session.getId(), closeStatus.toString(), ANSI.RESET);
        removeBot(session);
    }

    private void removeBot(WebSocketSession session) {
        //对map.values()的更改会作用于map
        BOT_MAP.values().removeIf(bot -> bot.getSession() == session);
    }


}
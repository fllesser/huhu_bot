package tech.chowyijiu.huhubot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.chowyijiu.huhubot.constant.ANSI;
import tech.chowyijiu.huhubot.event.Event;
import tech.chowyijiu.huhubot.event.meta.MetaEvent;
import tech.chowyijiu.huhubot.thread.ProcessEventTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Slf4j
public class Server extends TextWebSocketHandler {

    private static final List<Bot> bots = new ArrayList<>();

    public static int getConnections() {
        return bots.size();
    }

    /**
     * 根据userId获取Bot
     *
     * @param userId qq号
     * @return Bot
     */
    public static Bot getBot(Long userId) {
        for (Bot bot : bots)
            if (Objects.equals(bot.getUserId(), userId))
                return bot;
        return null;
    }

    /**
     * 获取所有Bot
     *
     * @return List<Bot>
     */
    public static List<Bot> getBots() {
        return bots;
    }

    public static void addBot(Long userId, WebSocketSession session) {
        bots.add(new Bot(userId, session));
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("{}GOCQ CONNECT SUCCESS, REMOTE[{}], CLIENT_NUM[{}]{}", ANSI.YELLOW,
                session.getRemoteAddress(), getConnections() + 1, ANSI.RESET);
    }

    @Override
    public void handleTextMessage(final @NotNull WebSocketSession session, final TextMessage message) {
        final String json = message.getPayload();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            Event event = Event.build(jsonObject);
            if (event == null) return;
            if (event instanceof MetaEvent metaEvent) {
                if (metaEvent.heartbeat()) return;//心跳忽略
                else if (metaEvent.connect()) {
                    //刚连接成功时，gocq会发一条消息给bot, 添加bot对象到bots中
                    addBot(event.getSelfId(), session);
                    log.info("{}RECEIVED GOCQ CLIENT[{}] CONNECTION SUCCESS MESSAGE{}", ANSI.YELLOW,
                            metaEvent.getSelfId(), ANSI.RESET);
                    return;
                }
            }
            //测试
            //if (bots.isEmpty() && event.getSelfId() == 888888L) addBot(event.getSelfId(), session);
            ProcessEventTask.execute(getBot(event.getSelfId()), event);
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
        bots.removeIf(bot -> bot.getSession() == session);
    }



}
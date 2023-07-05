package tech.chowyijiu.huhu_bot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.chowyijiu.huhu_bot.constant.ANSI;
import tech.chowyijiu.huhu_bot.constant.MetaTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.meta.MetaEvent;
import tech.chowyijiu.huhu_bot.thread.ProcessEventTask;
import tech.chowyijiu.huhu_bot.utils.LogUtil;

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
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        final String json = message.getPayload();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            Event event = Event.build(jsonObject);
            if (event == null) return;
            if (event instanceof MetaEvent) {
                MetaEvent metaEvent = (MetaEvent) event;
                if (Objects.equals(metaEvent.getMetaEventType(), MetaTypeEnum.heartbeat.name())) {
                    //心跳忽略
                    //log.info("[{}] bot[{}] heartbeat ", this.getClass().getSimpleName(), metaEvent.getSelfId());
                    return;
                } else if (Objects.equals(metaEvent.getMetaEventType(), MetaTypeEnum.lifecycle.name())
                        && Objects.equals(metaEvent.getSubType(), SubTypeEnum.connect.name())) {
                    //刚连接成功时，gocq会发一条消息给bot, 添加bot对象到bots中
                    addBot(event.getSelfId(), session);
                    log.info("{}RECEIVED GOCQ CLIENT[{}] CONNECTION SUCCESS MESSAGE{}", ANSI.YELLOW,
                            metaEvent.getSelfId(), ANSI.RESET);
                    return;
                }
            }
            //测试
            //if (bots.isEmpty() && event.getSelfId() == 888888L) addBot(event.getSelfId(), session);
            log.info("Accepted {}", event);
            for (Bot bot : bots)
                if (Objects.equals(bot.getSession(), session)) //这里不要用userId获取bot, 因为echoEvent没有self_id
                    ProcessEventTask.execute(bot, event);
        } catch (Exception e) {
            log.error("Parsing payload exception", e);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("{}connect exception sessionId[{}], exception[{}]{}",
                LogUtil.buildArgsWithColor(ANSI.YELLOW, session.getId(), exception.getMessage()));
        removeClient(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("{}connect close, sessionId:{},{}{}", ANSI.YELLOW,
                session.getId(), closeStatus.toString(), ANSI.RESET);
        removeClient(session);
    }

    private void removeClient(WebSocketSession session) {
        bots.removeIf(bot -> bot.getSession() == session);
    }

}
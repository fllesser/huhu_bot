package tech.flless.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import tech.flless.huhubot.core.constant.ANSI;
import tech.flless.huhubot.core.thread.ProcessEventTask;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Slf4j
public class OneBotV11Handler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        log.info("{}GOCQ CONNECT SUCCESS, REMOTE[{}], CLIENT_NUM[{}]{}", ANSI.YELLOW,
                session.getRemoteAddress(), BotContainer.getConnections() + 1, ANSI.RESET);
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
                    BotContainer.addBot(event.getSelfId(), session);
                    log.info("{}RECEIVED GOCQ CLIENT[{}] CONNECTION SUCCESS MESSAGE{}", ANSI.YELLOW,
                            metaEvent.getSelfId(), ANSI.RESET);
                    return;
                }
            }
            //测试
            //if (bots.isEmpty() && event.getSelfId() == 888888L) addBot(event.getSelfId(), session);
            Bot bot = BotContainer.getBot(event.getSelfId());
            log.info("<-ws-onebotv11-[{}]{}", bot.getSelfId(), event);
            event.setBot(bot);
            ProcessEventTask.execute(event);
        } catch (Exception e) {
            log.error("handleTextMessage exception", e);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("{}connect exception sessionId[{}], exception[{}]{}",
                ANSI.YELLOW, session.getId(), exception.getMessage(), ANSI.RESET);
        BotContainer.removeBot(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("{}connect close, sessionId:{},{}{}", ANSI.YELLOW,
                session.getId(), closeStatus.toString(), ANSI.RESET);
        BotContainer.removeBot(session);
    }


}
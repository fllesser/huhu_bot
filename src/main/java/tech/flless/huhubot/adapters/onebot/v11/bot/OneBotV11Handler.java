package tech.flless.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import tech.flless.huhubot.core.DispatcherCore;
import tech.flless.huhubot.core.thread.ProcessEventTask;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Slf4j
public class OneBotV11Handler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        log.info("OB11 CLIENT CONNECT SUCCESS, REMOTE[{}], CLIENT_NUM[{}]", session.getRemoteAddress(), BotContainer.getConnections() + 1);
    }

    @Override
    public void handleTextMessage(@NotNull final WebSocketSession session, final TextMessage message) {
        String json = message.getPayload();
        Event event = Event.build(JSONObject.parseObject(json));
        if (event == null) return;
        if (event instanceof MetaEvent metaEvent) {
            if (metaEvent.isHeartbeat()) return;//心跳忽略
            else if (metaEvent.isConnected()) {
                //刚连接成功时，onebot实现端会发一条消息给bot, 添加bot到map中
                BotContainer.addBot(event.getSelfId(), session);
                log.info("Received OnebotV11 Client[{}] Connection Success Message", metaEvent.getSelfId());
                return;
            }
        }
        log.info("[hb]<-ws-[ob-{}] {}", event.getSelfId(), event);
        event.setBot(BotContainer.getBot(event.getSelfId()));
        ProcessEventTask.dispatch(event);
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Connection Happened Exception SessionId[{}], Exception[{}]", session.getId(), exception.getMessage());
        BotContainer.removeBot(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.error("Connection Closed, SessionId:{},{}", session.getId(), closeStatus.toString());
        BotContainer.removeBot(session);
    }


}
package com.github.huhubot.adapters.onebot.v11.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Slf4j
public class OneBotV11Handler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        log.info("OnebotV11 Client Connect Success, Remote[{}], Client_Num[{}]", session.getRemoteAddress(), BotContainer.getConnections() + 1);
    }

    @Override
    public void handleTextMessage(@NotNull final WebSocketSession session, final TextMessage message) {
        ProcessEventTask.dispatch(message.getPayload(), session);
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
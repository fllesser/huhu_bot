package com.github.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import com.github.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import com.github.huhubot.core.DispatcherCore;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import com.github.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import com.github.huhubot.utils.IocUtil;
import com.github.huhubot.utils.ThreadPoolUtil;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public record ProcessEventTask(String json, WebSocketSession session) implements Runnable {

    private static final DispatcherCore DISPATCHER_CORE;

    static {
        DISPATCHER_CORE = IocUtil.getBean(DispatcherCore.class);
    }

    //@Override
    public void run() {
        JSONObject jsonObject = JSONObject.parseObject(json);
        Event event = Event.build(jsonObject);
        if (event == null) {
            Bot.transferData(jsonObject.getLong("echo"), jsonObject.get("data"));
            return;
        }
        event.setBot(BotContainer.getBot(event.getSelfId()));
        log.info("[hb]<-ws-[ob-{}] {}", event.getSelfId(), event);
        if (event instanceof MessageEvent me) {
            DISPATCHER_CORE.onMessage(me);
        } else if (event instanceof NoticeEvent ne) {
            DISPATCHER_CORE.onNotice(ne);
        } else if (event instanceof MetaEvent me) {
            if (me.isConnected()) {
                //刚连接成功时，onebot实现端会发一条消息给bot, 添加bot到map中
                BotContainer.addBot(event.getSelfId(), session);
                log.info("Received OnebotV11 Client[{}] Connection Success Message", me.getSelfId());
            }
        }
//        else if (event instanceof RequestEvent requestEvent) {
//            log.info("[hb]<-ws-[ob-{}] {}", requestEvent);
//        }

    }

    public static void dispatch(String json, WebSocketSession session) {
        ThreadPoolUtil.ProcessEventExecutor.execute(new ProcessEventTask(json, session));
    }


    @Override
    public String toString() {
        return json;
    }
}
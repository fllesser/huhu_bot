package tech.flless.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.flless.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import tech.flless.huhubot.core.DispatcherCore;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import tech.flless.huhubot.utils.IocUtil;
import tech.flless.huhubot.utils.ThreadPoolUtil;

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
        Event event = Event.build(JSONObject.parseObject(json));
        if (event == null) return;
 log.info("[hb]<-ws-[ob-{}] {}", event.getSelfId(), event);

        event.setBot(BotContainer.getBot(event.getSelfId()));
        if (event instanceof MessageEvent messageEvent) {
            DISPATCHER_CORE.onMessage(messageEvent);
        } else if (event instanceof NoticeEvent noticeEvent) {
            DISPATCHER_CORE.onNotice(noticeEvent);
        } else if (event instanceof RequestEvent requestEvent) {
            log.info("{}", requestEvent);
        } else if (event instanceof MetaEvent metaEvent) {
            if (metaEvent.isHeartbeat()) return;//心跳忽略
            else if (metaEvent.isConnected()) {
                //刚连接成功时，onebot实现端会发一条消息给bot, 添加bot到map中
                BotContainer.addBot(event.getSelfId(), session);
                log.info("Received OnebotV11 Client[{}] Connection Success Message", metaEvent.getSelfId());
                return;
            }
        }
       
    }

    public static void dispatch(String json, WebSocketSession session) {
        ThreadPoolUtil.ProcessEventExecutor.execute(new ProcessEventTask(json, session));
    }


}
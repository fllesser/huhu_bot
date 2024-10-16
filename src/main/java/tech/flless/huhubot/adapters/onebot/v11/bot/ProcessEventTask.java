package tech.flless.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
public record ProcessEventTask(Event event) implements Runnable {

    private static final DispatcherCore DISPATCHER_CORE;

    static {
        DISPATCHER_CORE = IocUtil.getBean(DispatcherCore.class);
    }

    //@Override
    public void run() {

        if (event instanceof MessageEvent messageEvent) {
            DISPATCHER_CORE.onMessage(messageEvent);
        } else if (event instanceof NoticeEvent noticeEvent) {
            DISPATCHER_CORE.onNotice(noticeEvent);
        } else if (event instanceof RequestEvent requestEvent) {
            log.info("{}", requestEvent);
        }
    }

    public static void execute(final Event event) {

        ThreadPoolUtil.ProcessEventExecutor.execute(new ProcessEventTask(event));
    }


}
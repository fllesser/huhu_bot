package tech.chowyijiu.huhubot.core.thread;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.core.DispatcherCore;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.event.notice.NoticeEvent;
import tech.chowyijiu.huhubot.core.event.request.RequestEvent;
import tech.chowyijiu.huhubot.utils.IocUtil;
import tech.chowyijiu.huhubot.utils.ThreadPoolUtil;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@SuppressWarnings("all")
@ToString
public class ProcessEventTask implements Runnable {

    private final Event event;

    private ProcessEventTask(Event event) {
        this.event = event;
    }

    private static final ThreadPoolExecutor EVENT_EXECUTOR;
    private static final DispatcherCore DISPATCHER_CORE;

    static {
        EVENT_EXECUTOR = ThreadPoolUtil.getEventExecutor();
        DISPATCHER_CORE = IocUtil.getBean(DispatcherCore.class);
    }
    //
    @Override
    public void run() {
        log.info("{} starts to match handler", event);
        if (event instanceof MessageEvent messageEvent) {
            DISPATCHER_CORE.onMessage(messageEvent);
        } else if (event instanceof NoticeEvent noticeEvent) {
            DISPATCHER_CORE.onNotice(noticeEvent);
        } else if (event instanceof RequestEvent requestEvent) {
            log.info("{}", requestEvent);
        }
        log.info("{} matches handler end", event);
    }

    public static void execute(final Event event) {
        EVENT_EXECUTOR.execute(new ProcessEventTask(event));
    }

}
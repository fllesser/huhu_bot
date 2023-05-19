package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import tech.chowyijiu.huhu_bot.core.DispatcherCore;
import tech.chowyijiu.huhu_bot.event.EchoEvent;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.RequestEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.utils.GocqSyncRequestUtil;
import tech.chowyijiu.huhu_bot.utils.IocUtil;
import tech.chowyijiu.huhu_bot.utils.ThreadPoolUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class ProcessEventTask implements Runnable {

    private final Bot bot;
    private final Event event;

    private ProcessEventTask(Bot bot, Event event) {
        this.bot = bot;
        this.event = event;
    }

    private static final ThreadPoolExecutor THREAD_POOL;
    private static final DispatcherCore DISPATCHER_CORE;

    static {
        THREAD_POOL = ThreadPoolUtil.getExecutor();
        DISPATCHER_CORE = IocUtil.getBean(DispatcherCore.class);
    }

    @Override
    public void run() {
        try {
            //log.info(" {} will be preProcessed", eventTypeEnum);
            if (event instanceof MessageEvent) {
                DISPATCHER_CORE.matchMessageHandler(bot, ((MessageEvent) event));
            } else if (event instanceof NoticeEvent) {
                DISPATCHER_CORE.matchNoticeHandler(bot, ((NoticeEvent) event));
            } else if (event instanceof RequestEvent) {
                log.info("[RequestEvent] {}", event);
            } else if (event instanceof EchoEvent) {
                EchoEvent echoEvent = (EchoEvent) this.event;
                String echo = echoEvent.getEcho();
                if (Strings.isNotBlank(echo)) {
                    GocqSyncRequestUtil.putEchoResult(echo, echoEvent.getData());
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred in preprocessing event, Exception:", e);
        }

    }

    public static void execute(final Bot bot, final Event event) {
        THREAD_POOL.execute(new ProcessEventTask(bot, event));
    }


}
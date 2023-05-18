package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.core.DispatcherCore;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.notice.NoticeEvent;
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
    private final String original;

    private ProcessEventTask(Bot bot, Event event, String original) {
        this.bot = bot;
        this.event = event;
        this.original = original;
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
            //log.info("[{}] {} will be preProcessed", this.getClass().getSimpleName(), eventTypeEnum);
            if (event instanceof MessageEvent) {
                DISPATCHER_CORE.matchMessageHandler(bot, ((MessageEvent) event));
            } else if (event instanceof NoticeEvent) {
                DISPATCHER_CORE.matchNoticeHandler(bot, ((NoticeEvent) event));
            }
        } catch (Exception e) {
            log.error("[{}] Exception occurred in preprocessing event, Exception:", this.getClass().getSimpleName(), e);
        }

    }

    public static void execute(final Bot bot, final Event event, final String original) {
        THREAD_POOL.execute(new ProcessEventTask(bot, event, original));
    }


}
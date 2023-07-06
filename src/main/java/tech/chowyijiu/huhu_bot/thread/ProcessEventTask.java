package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.core.CoreDispatcher;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.event.request.RequestEvent;
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

    @Override
    public String toString() {
        return "ProcessEventTask{" +
                "bot=" + bot.getUserId() +
                ", event=" + event +
                '}';
    }

    private ProcessEventTask(Bot bot, Event event) {
        this.bot = bot;
        this.event = event;
    }

    private static final ThreadPoolExecutor EVENT_EXECUTOR;
    private static final CoreDispatcher CORE_DISPATCHER;

    static {
        EVENT_EXECUTOR = ThreadPoolUtil.getEventExecutor();
        CORE_DISPATCHER = IocUtil.getBean(CoreDispatcher.class);
    }

    @Override
    public void run() {
        try {
            if (event instanceof MessageEvent) {
                CORE_DISPATCHER.onMessage(bot, (MessageEvent) event);
            } else if (event instanceof NoticeEvent) {
                CORE_DISPATCHER.onNotice(bot, ((NoticeEvent) event));
            } else if (event instanceof RequestEvent) {
                log.info("{}", event);
            }
        } catch (Exception e) {
            log.error("Exception occurred in preprocessing {}, Exception:{}", event, e);
        }

    }

    public static void execute(final Bot bot, final Event event) {
        EVENT_EXECUTOR.execute(new ProcessEventTask(bot, event));
    }


}
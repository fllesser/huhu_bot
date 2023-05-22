package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import tech.chowyijiu.huhu_bot.core.DispatcherCore;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.echo.EchoEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.event.request.RequestEvent;
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
            if (event instanceof MessageEvent) {
                DISPATCHER_CORE.onMessage(bot, (MessageEvent) this.event);
            } else if (event instanceof NoticeEvent) {
                DISPATCHER_CORE.onNotice(bot, ((NoticeEvent) this.event));
            } else if (event instanceof EchoEvent) {
                EchoEvent echoEvent = (EchoEvent) this.event;
                String echo = echoEvent.getEcho();
                if (StringUtils.hasLength(echo)) {
                    GocqSyncRequestUtil.putEchoResult(echo, echoEvent.getData());
                }
            } else if (event instanceof RequestEvent) {
                log.info("{}", event);
            }
        } catch (Exception e) {
            log.error("Exception occurred in preprocessing {}, Exception:{}", event, e);
        }

    }

    public static void execute(final Bot bot, final Event event) {
        THREAD_POOL.execute(new ProcessEventTask(bot, event));
    }


}
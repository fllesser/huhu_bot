package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tech.chowyijiu.huhu_bot.constant.EventTypeEnum;
import tech.chowyijiu.huhu_bot.core.DispatcherCore;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;
import tech.chowyijiu.huhu_bot.utils.IocUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static final ThreadPoolExecutor threadPool;
    private static final DispatcherCore DISPATCHER_CORE;

    static {
        DISPATCHER_CORE = IocUtil.getBean(DispatcherCore.class);
        threadPool = new ThreadPoolExecutor(16, 31,
                10L * 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(160),
                new CustomizableThreadFactory("pool-processMessage-"),
                new ShareRunsPolicy("pool-processMessage")
        );
    }

    @Override
    public void run() {
        try {
            EventTypeEnum eventTypeEnum = EventTypeEnum.valueOf(event.getClass().getSimpleName());
            //log.info("[{}] {} will be preProcessed", this.getClass().getSimpleName(), eventTypeEnum);
            switch (eventTypeEnum) {
                case GroupMessageEvent:
                case PrivateMessageEvent:
                    //todo 命令传参数 args
                    DISPATCHER_CORE.matchMessageHandler(bot, ((MessageEvent) event));
                    break;
                case NoticeEvent:
                    DISPATCHER_CORE.matchNoticeHandler(bot, ((NoticeEvent) event));
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("[{}] Exception occurred in preprocessing event, Exception:", this.getClass().getSimpleName(), e);
        }

    }

    public static void execute(final Bot bot, final Event event, final String original) {
        threadPool.execute(new ProcessEventTask(bot, event, original));
    }


}
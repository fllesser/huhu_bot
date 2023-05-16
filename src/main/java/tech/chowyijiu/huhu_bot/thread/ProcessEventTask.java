package tech.chowyijiu.huhu_bot.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.EventTypeEnum;
import tech.chowyijiu.huhu_bot.constant.MetaTypeEnum;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.*;
import tech.chowyijiu.huhu_bot.handler.HandlerContainer;
import tech.chowyijiu.huhu_bot.utils.IocUtil;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class ProcessEventTask implements Runnable {

    private final WebSocketSession session;
    private final Event event;
    private final String original;

    private ProcessEventTask(WebSocketSession session, Event event, String original) {
        this.session = session;
        this.event = event;
        this.original = original;
    }

    private static final ThreadPoolExecutor threadPool;
    private static final HandlerContainer handlerContainer;

    static {
        handlerContainer = IocUtil.getBean(HandlerContainer.class);
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
                case MetaEvent:
                    preProcessMetaEvent();
                    break;
                case GroupMessageEvent:
                    handlerContainer.matchMessageHandler(session, ((GroupMessageEvent) event));
                    break;
                case PrivateMessageEvent:
                    handlerContainer.matchMessageHandler(session, ((PrivateMessageEvent) event));
                    break;
                case NoticeEvent:
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("[{}] Exception occurred in preprocessing event, Exception:", this.getClass().getSimpleName(), e);
        }

    }

    /**
     * 预处理 MetaEvent
     */
    public void preProcessMetaEvent() {
        MetaEvent metaEvent = (MetaEvent) this.event;
        switch (MetaTypeEnum.valueOf(metaEvent.getMetaEventType())) {
            case heartbeat:
                //心跳
                //log.info("[{}] bot[{}] heartbeat ", this.getClass().getSimpleName(), metaEvent.getSelfId());
                break;
            case lifecycle:
                if (Objects.equals(metaEvent.getSubType(), SubTypeEnum.connect.name())) {
                    //刚连接成功时，gocq会发一条消息给bot
                    log.info("[{}] bot[{}] received gocq connection success message ", this.getClass().getSimpleName(), metaEvent.getSelfId());
                }
                break;
        }
    }

    /**
     * 预处理NoticeEvent
     */
    public void preProcessNoticeEvent() {
        NoticeEvent noticeEvent = (NoticeEvent) this.event;
        switch (NoticeTypeEnum.valueOf(noticeEvent.getNoticeType())) {
            case group_decrease:
            case friend_recall:
            case client_status:
            case essence:
            case offline_file:
        }
    }


    public static void execute(final WebSocketSession session, final Event event, final String original) {
        threadPool.execute(new ProcessEventTask(session, event, original));
    }


}
package tech.chowyijiu.huhu_bot.thread;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.*;
import tech.chowyijiu.huhu_bot.dispenser.MessageDispenser;
import tech.chowyijiu.huhu_bot.dispenser.NoticeDispenser;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MetaEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;
import tech.chowyijiu.huhu_bot.utils.GocqSyncRequestUtil;
import tech.chowyijiu.huhu_bot.utils.IocUtil;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.util.Locale;
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
    private final Message bean;
    private Event event;
    private final String original;

    private ProcessEventTask(WebSocketSession session, Message bean, String original) {
        this.session = session;
        this.bean = bean;
        this.original = original;
    }

    private static final ThreadPoolExecutor threadPool;
    private static final MessageDispenser messageDispenser;
    private static final NoticeDispenser noticeDispenser;

    static {
        messageDispenser = IocUtil.getBean(MessageDispenser.class);
        noticeDispenser = IocUtil.getBean(NoticeDispenser.class);
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
            switch (EventTypeEnum.valueOf(event.getClass().getSimpleName())) {
                case MetaEvent:
                    preProcessMetaEvent();
                    break;
                case MessageEvent:
                    preProcessMessageEvent();
                    break;
                case GroupMessageEvent:
                case PrivateMessageEvent:
                case NoticeEvent:
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("处理消息时异常", e);
        }

    }

    public void oldRun() {
        try {
            if (PostTypeEnum.message.name().equals(bean.getPostType())) {
                // 普通消息
                final String rawMessage = bean.getRawMessage();
                log.info("[{}] 收到来自用户[{}]的消息: [{}]", bean.getMessageType().toUpperCase(Locale.ROOT)
                        , bean.getUserId(), rawMessage);
                if (rawMessage != null) {
                    messageDispenser.dispense(session, bean, rawMessage);
                }
            } else if (PostTypeEnum.notice.name().equals(bean.getPostType())) {
                // bot通知
                log.info("[{}] event will be handed over to the reminder NoticeDispenser for processing", bean.getNoticeType());
                noticeDispenser.dispense(session, bean);
            } else if (PostTypeEnum.meta_event.toString().equals(bean.getPostType())) {
                // 系统消息
                if (MetaTypeEnum.lifecycle.name().equals(bean.getMetaEventType())
                        && SubTypeEnum.connect.name().equals(bean.getSubType())) {
                    // 刚连接成功时，gocq会发一条消息给bot
                    Server.putUserIdMap(session.getId(), bean.getSelfId());
                }
            } else {
                JSONObject jsonObject = JSONObject.parseObject(original);
                String echo = jsonObject.getString("echo");
                if (Strings.isNotBlank(echo)) {
                    GocqSyncRequestUtil.putEchoResult(echo, jsonObject);
                }
            }
        } catch (Exception e) {
            log.error("处理消息时异常", e);
        }
    }

    /**
     * 预处理 MessageEvent
     */
    public void preProcessMessageEvent() {
        MessageEvent messageEvent = (MessageEvent) this.event;

    }

    /**
     * 预处理 MetaEvent
     */
    public void preProcessMetaEvent() {
        MetaEvent metaEvent = (MetaEvent) this.event;
        switch (MetaTypeEnum.valueOf(metaEvent.getMetaEventType())) {
            case heartbeat:
                //心跳忽略
                break;
            case lifecycle:
                if (Objects.equals(metaEvent.getSubType(), SubTypeEnum.connect.name())) {
                    // 刚连接成功时，gocq会发一条消息给bot
                    log.info("GOCQ 连接成功, meta_event :{}", metaEvent);
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


    public static void execute(final WebSocketSession session, final Message bean, final String original) {
        threadPool.execute(new ProcessEventTask(session, bean, original));
    }


}
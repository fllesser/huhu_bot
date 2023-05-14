package tech.chowyijiu.huhu_bot.thread;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.MetaTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.dispenser.MessageDispenser;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
import tech.chowyijiu.huhu_bot.utils.GocqSyncRequestUtil;
import tech.chowyijiu.huhu_bot.utils.IocUtil;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class ProcessMessageTask implements Runnable {

    private final WebSocketSession session;
    private final Message bean;
    private final String original;

    private ProcessMessageTask(WebSocketSession session, Message bean, String original) {
        this.session = session;
        this.bean = bean;
        this.original = original;
    }

    private final static ThreadPoolExecutor threadPool;

    private static final MessageDispenser messageDispenser;

    //private static final NoticeDispenser noticeDispenser;
    static {
        messageDispenser = IocUtil.getBean(MessageDispenser.class);
        //noticeDispenser = ApplicationContextProvider.getBean(NoticeDispenser.class);
        threadPool = new ThreadPoolExecutor(16, 31, 10L * 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(160),
                new CustomizableThreadFactory("pool-processMessage-"),
                new ShareRunsPolicy("pool-processMessage"));
    }

    @Override
    public void run() {
        try {
            if (PostTypeEnum.message.toString().equals(bean.getPostType())) {
                // 普通消息
                final String rawMessage = bean.getRawMessage();
                log.info("[{}] 收到来自用户 [{}] 的消息: {}", bean.getMessageType(), bean.getUserId(), rawMessage);
                if (rawMessage != null) {
                    messageDispenser.onEvent(session, bean, rawMessage);
                }
            } else if (PostTypeEnum.notice.toString().equals(bean.getPostType())) {
                // bot通知
                //noticeDispenser.onEvent(session, bean);
            } else if (PostTypeEnum.meta_event.toString().equals(bean.getPostType())) {
                // 系统消息
                if (MetaTypeEnum.lifecycle.toString().equals(bean.getMetaEventType()) && SubTypeEnum.connect.toString().equals(bean.getSubType())) {
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

    public static void execute(final WebSocketSession session, final Message bean, final String original) {
        threadPool.execute(new ProcessMessageTask(session, bean, original));
    }



}
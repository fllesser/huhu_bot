package com.github.huhubot.adapters.onebot.v11.bot;

import com.alibaba.fastjson2.JSONObject;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;
import com.github.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import com.github.huhubot.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import com.github.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import com.github.huhubot.core.DispatcherCore;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import com.github.huhubot.utils.IocUtil;
import com.github.huhubot.utils.ThreadPoolUtil;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public record ProcessEventTask(String json, WebSocketSession session) implements Runnable {

    private static final DispatcherCore dispatcherCore;

    static {
        dispatcherCore = IocUtil.getBean(DispatcherCore.class);
    }

    @Override
    public void run() {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String postType = jsonObject.getString("post_type");
        if (!StringUtil.hasLength(postType)) {
            Bot.setAndNotify(jsonObject.getLong("echo"), jsonObject.get("data"));
            return;
        }
        switch (PostTypeEnum.valueOf(postType)) {
            case message_sent, message -> {
                MessageEvent event = MessageEvent.build(jsonObject);
                dispatcherCore.onMessage(event);
            }
            case notice -> {
                NoticeEvent event = NoticeEvent.build(jsonObject);
                dispatcherCore.onNotice(event);
            }
            case meta_event -> {
                MetaEvent event = jsonObject.toJavaObject(MetaEvent.class);
                if (event.isConnected()) {
                    //刚连接成功时，onebot 实现端会发一条消息给 bot, 添加 bot 到容器中
                    BotContainer.addBot(event.getSelfId(), session);
                    log.info("Received OnebotV11 Client[{}] Connection Success Message", event.getSelfId());
                }
            }
            case request -> {
                RequestEvent event = jsonObject.toJavaObject(RequestEvent.class);
            }
        }
    }

    public static void dispatch(String json, WebSocketSession session) {
        ThreadPoolUtil.ProcessEventExecutor.execute(new ProcessEventTask(json, session));
    }


    @Override
    public String toString() {
        return json;
    }
}
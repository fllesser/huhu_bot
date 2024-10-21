package com.github.huhubot.adapters.onebot.v11.event.message;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.huhubot.adapters.onebot.v11.bot.BotContainer;
import com.github.huhubot.core.exception.FinishedException;
import lombok.Getter;
import lombok.Setter;
import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.adapters.onebot.v11.entity.response.MessageInfo;
import com.github.huhubot.adapters.onebot.v11.entity.response.Sender;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * @author elastic chow
 * @date 16/5/2023
 */

@Slf4j
@Getter
@Setter
public class MessageEvent extends Event {

    @JsonProperty("post_type")
    private String postType; //message message_sent
    @JsonProperty("sub_type")
    private String subType; //group, public
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("message_id")
    private Integer messageId;
    //注意gocq, config.yml中要设置数组array消息格式
    @JsonProperty("message")
    private Message message;
    @JsonProperty("raw_message")
    private String rawMessage;
    @JsonProperty("font")
    private Integer font; //0
    @JsonProperty("sender")
    private Sender sender;

    //去除匹配的命令, 保留剩余的字符串,并去掉头尾空格, 注意不会去除at
    @JsonIgnore
    private String commandArgs;

    public static MessageEvent build(final JSONObject jsonObject) {
        String messageType = jsonObject.getString("message_type");
        MessageEvent event;
        if ("private".equals(messageType)) {
            event = jsonObject.toJavaObject(PrivateMessageEvent.class);
        } else {
            event = jsonObject.toJavaObject(GroupMessageEvent.class);
        }
        event.init();
        return event;
    }

    public MessageInfo getReply() {
        MessageSegment segment = this.message.get("reply", 0);
        if (segment == null) return null;
        return this.getBot().getMsg(segment.getInteger("id"));
    }


    public void reply(Object message) {

    }

    //中断handler，并回复触发者
    public void finish(String message) {
        throw new FinishedException(message);
    }




}

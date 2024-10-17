package com.github.huhubot.adapters.onebot.v11.event;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.github.huhubot.adapters.onebot.v11.bot.Bot;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import com.github.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;
import com.github.huhubot.utils.StringUtil;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Slf4j
@Getter
@Setter
public abstract class Event {

    @JsonIgnore
    private JSONObject eventJsonObject;

    @JsonIgnore
    private Bot bot;

    @JsonProperty("self_id")
    private Long selfId;
    @JsonProperty("post_type")
    private String postType;
    private Long time;

    public static Event build(final JSONObject jsonObject) {
        String postType = jsonObject.getString("post_type");
        if (StringUtil.hasLength(postType)) {
            Event event = switch (PostTypeEnum.valueOf(postType)) {
                case message_sent, message -> MessageEvent.build(jsonObject);
                case notice -> NoticeEvent.build(jsonObject);
                case request -> jsonObject.toJavaObject(RequestEvent.class);
                case meta_event -> jsonObject.toJavaObject(MetaEvent.class);
            };
            event.setEventJsonObject(jsonObject);
            return event;
        } else return null;
    }
}

package tech.flless.huhubot.adapters.onebot.v11.event;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import tech.flless.huhubot.adapters.onebot.v11.bot.Bot;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import tech.flless.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import tech.flless.huhubot.adapters.onebot.v11.constant.PostTypeEnum;
import tech.flless.huhubot.utils.StringUtil;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
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
        Event event = null;
        if (StringUtil.hasLength(postType)) {
            event = switch (PostTypeEnum.valueOf(postType)) {
                case message_sent, message -> MessageEvent.build(jsonObject);
                case notice -> NoticeEvent.build(jsonObject);
                case request -> jsonObject.toJavaObject(RequestEvent.class);
                case meta_event -> jsonObject.toJavaObject(MetaEvent.class);
            };
            event.setEventJsonObject(jsonObject);
        } else Bot.transferData(jsonObject.getLong("echo"), jsonObject.getString("data"));
        return event;
    }

}

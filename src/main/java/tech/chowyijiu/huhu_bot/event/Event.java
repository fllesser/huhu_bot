package tech.chowyijiu.huhu_bot.event;


import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
public abstract class Event {

    private JSONObject jsonObject;

    private Long selfId;
    private String postType;
    private Long time;

    public static Event jsonToEvent(JSONObject jsonObject) {
        String postType = jsonObject.getString("post_type");
        Event event = null;
        if (postType == null && jsonObject.getString("echo") != null) {
            event = jsonObject.toJavaObject(EchoEvent.class);
        } else {
            if (Objects.equals(postType, PostTypeEnum.message.name())) {
                event = MessageEvent.jsonToMessageEvent(jsonObject);
            } else if (Objects.equals(postType, PostTypeEnum.notice.name())) {
                event = NoticeEvent.jsonToNoticeEvent(jsonObject);
            } else if (Objects.equals(postType, PostTypeEnum.request.name())) {
                event = jsonObject.toJavaObject(RequestEvent.class);
            } else if (Objects.equals(postType, PostTypeEnum.meta_event.name())) {
                event = jsonObject.toJavaObject(MetaEvent.class);
            }
        }
        if (event != null) {
            event.setJsonObject(jsonObject);
        }
        return event;
    }

}

package tech.chowyijiu.huhu_bot.event;


import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.echo.EchoEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.meta.MetaEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.event.request.RequestEvent;

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
            switch (PostTypeEnum.valueOf(postType)) {
                case message:
                    event = MessageEvent.jsonToMessageEvent(jsonObject);
                    break;
                case notice:
                    event = NoticeEvent.jsonToNoticeEvent(jsonObject);
                    break;
                case request:
                    event = jsonObject.toJavaObject(RequestEvent.class);
                    break;
                case meta_event:
                    event = jsonObject.toJavaObject(MetaEvent.class);
                    break;
            }
        }
        if (event != null) {
            event.setJsonObject(jsonObject);
        }
        return event;
    }

}

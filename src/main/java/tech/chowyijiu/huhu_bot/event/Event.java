package tech.chowyijiu.huhu_bot.event;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.meta.MetaEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.event.request.RequestEvent;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
public abstract class Event {

    @JsonIgnore
    private JSONObject jsonObject;

    private Long selfId;
    private String postType;
    private Long time;

    public static Event build(JSONObject jsonObject) {
        String postType = jsonObject.getString("post_type");
        Event event = null;
        if (StringUtil.hasLength(postType)) {
            event = switch (PostTypeEnum.valueOf(postType)) {
                case message_sent, message -> MessageEvent.build(jsonObject);
                case notice -> NoticeEvent.build(jsonObject);
                case request -> jsonObject.toJavaObject(RequestEvent.class);
                case meta_event -> jsonObject.toJavaObject(MetaEvent.class);
            };
            event.setJsonObject(jsonObject);
        } else {
            StringUtil.hasLength(jsonObject.getString("echo"), echo -> {
                ApiResp resp = jsonObject.toJavaObject(ApiResp.class);
                if ("ok".equals(resp.getStatus())) Bot.putEchoResult(echo, resp.getData());
            });
        }
        return event;
    }

}

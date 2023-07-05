package tech.chowyijiu.huhu_bot.event;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.echo.EchoEvent;
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
            switch (PostTypeEnum.valueOf(postType)) {
                case message_sent:
                case message:
                    event = MessageEvent.build(jsonObject);
                    break;
                case notice:
                    event = NoticeEvent.build(jsonObject);
                    break;
                case request:
                    event = jsonObject.toJavaObject(RequestEvent.class);
                    break;
                case meta_event:
                    event = jsonObject.toJavaObject(MetaEvent.class);
                    break;
            }
            event.setJsonObject(jsonObject);
        } else {
            //todo put的操作放这里来?
            if (StringUtil.hasLength(jsonObject.getString("echo")))  {
                EchoEvent echoEvent = jsonObject.toJavaObject(EchoEvent.class);
                if ("ok".equals(echoEvent.getStatus()))
                    Bot.putEchoResult(echoEvent.getEcho(), echoEvent.getData());
            }
        }
        return event;
    }

}

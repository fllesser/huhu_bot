package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.response.WsResp;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString(exclude = {"userId", "groupId"})
public abstract class Event {

    private Long selfId;
    private String postType;
    private Long time;

    //方便发送消息
    private Long userId;
    private Long groupId;

    public static Event respToEvent(WsResp wsResp) {
        if (Objects.equals(wsResp.getPostType(), PostTypeEnum.message.name())) {
            return MessageEvent.respToEvent(wsResp);
        } else if (Objects.equals(wsResp.getPostType(), PostTypeEnum.notice.name())) {
            return NoticeEvent.respToEvent(wsResp);
        } else if (Objects.equals(wsResp.getPostType(), PostTypeEnum.meta_event.name())) {
            return MetaEvent.respToEvent(wsResp);
        }
        return null;
    }

}

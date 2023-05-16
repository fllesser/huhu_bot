package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString
public class Event {

    private Long selfId;
    private String postType;
    private Long time;

    public static Event respToEvent(MessageResp messageResp) {
        if (Objects.equals(messageResp.getPostType(), PostTypeEnum.message.name())) {
            return MessageEvent.respToEvent(messageResp);
        } else if (Objects.equals(messageResp.getPostType(), PostTypeEnum.notice.name())) {
            return NoticeEvent.respToEvent(messageResp);
        } else if (Objects.equals(messageResp.getPostType(), PostTypeEnum.meta_event.name())) {
            return MetaEvent.respToEvent(messageResp);
        }
        Event event = new Event();
        BeanUtils.copyProperties(messageResp, event);
        return event;
    }
}

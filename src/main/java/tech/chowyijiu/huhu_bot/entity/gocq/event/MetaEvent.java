package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.WsResp;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MetaEvent extends Event {

    private final String postType = PostTypeEnum.meta_event.name();
    private String metaEventType;
    private String subType;

    public static MetaEvent respToEvent(WsResp wsResp) {
        MetaEvent metaEvent = new MetaEvent();
        BeanUtils.copyProperties(wsResp, metaEvent);
        return metaEvent;
    }
}

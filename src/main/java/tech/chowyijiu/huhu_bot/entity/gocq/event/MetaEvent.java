package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class MetaEvent extends Event {

    private String metaEventType;
    private String subType;

    public static MetaEvent respToEvent(MessageResp messageResp) {
        MetaEvent metaEvent = new MetaEvent();
        BeanUtils.copyProperties(messageResp, metaEvent);
        return metaEvent;
    }
}

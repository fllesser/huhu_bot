package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@NoArgsConstructor
public class PrivateMessageEvent extends MessageEvent {

    public PrivateMessageEvent(MessageResp messageResp) {
        PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent();
        BeanUtils.copyProperties(messageResp, privateMessageEvent);
    }
}

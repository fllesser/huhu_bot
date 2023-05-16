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
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class PrivateMessageEvent extends MessageEvent {

    public PrivateMessageEvent(MessageResp messageResp) {
        BeanUtils.copyProperties(messageResp, this);
    }
}

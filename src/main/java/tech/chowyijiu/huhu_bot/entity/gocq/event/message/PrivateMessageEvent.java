package tech.chowyijiu.huhu_bot.entity.gocq.event.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.WsResp;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class PrivateMessageEvent extends MessageEvent {

    private final String messageType = MessageTypeEnum.private_.getType();

    public PrivateMessageEvent(WsResp wsResp) {
        BeanUtils.copyProperties(wsResp, this);
    }
}

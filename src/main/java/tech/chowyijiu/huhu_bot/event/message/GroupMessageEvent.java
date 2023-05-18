package tech.chowyijiu.huhu_bot.event.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class GroupMessageEvent extends MessageEvent {

    private final String messageType = MessageTypeEnum.group.getType();
    private Long groupId;
    private String anonymous;
    private boolean toMe;

}

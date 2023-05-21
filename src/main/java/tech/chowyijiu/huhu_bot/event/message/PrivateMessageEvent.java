package tech.chowyijiu.huhu_bot.event.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Setter
@Getter
@NoArgsConstructor
public class PrivateMessageEvent extends MessageEvent {

    private final String messageType = MessageTypeEnum.private_.getType();

    private String subType; // 消息子类型, 如果是好友则是 friend, 如果是群临时会话则是 group, 如果是在群中自身发送则是 group_self

    @JSONField(name = "temp_source")
    private Long tempSource;

    @Override
    public String toString() {
        return "PrivateMessageEvent{" +
                "userId=" + super.getUserId() +
                ", message=" + super.getMessage() +
                ", subType=" + subType +
                "}";
    }
}

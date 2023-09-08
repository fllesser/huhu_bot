package tech.chowyijiu.huhubot.core.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Setter
@Getter
@NoArgsConstructor
public class PrivateMessageEvent extends MessageEvent {

    private final String messageType = "private";

    // 消息子类型
    // 如果是好友则是 friend
    // 如果是群临时会话则是 group
    // 如果是在群中自身发送则是 group_self
    @JsonProperty("sub_type") private String subType;

    //好像gocq那边的数据有问题, 默认0
    @JsonProperty("temp_source") private Integer tempSource;

    @Override
    public String toString() {
        return "PrivateMessageEvent{" +
                "userId=" + super.getUserId() +
                ", raw_message=" + super.getRawMessage() +
                ", subType=" + subType +
                "}";
    }
}

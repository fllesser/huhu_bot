package tech.flless.huhubot.adapters.onebot.v11.event.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.MessageSegment;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Setter
@Getter
@NoArgsConstructor
public class GroupMessageEvent extends MessageEvent {

    private final String messageType = "group";

    // 消息子类型, 正常消息是 normal,
    // 匿名消息是 anonymous,
    // 系统提示 ( 如「管理员已禁止群内匿名聊天」 ) 是 notice
    @JsonProperty("sub_type")
    private String subType;
    @JsonProperty("group_id")
    private Long groupId;
    //匿名信息
    @JsonProperty("anonymous")
    private String anonymous;
    //是否at bot
    @JsonIgnore
    private Boolean toMe = null;

    public boolean isToMe() {
        if (toMe == null) toMe = this.getMessage().checkToMe(this.getSelfId());
        return toMe;
    }

    @Override
    public String toString() {
        return "GroupMessageEvent{" +
                "gid=" + groupId +
                ", uid=" + super.getUserId() +
                ", raw_msg=" + super.getRawMessage() +
                "}";
    }

    @Override
    public void reply(Object message) {
        getBot().sendGroupMessage(this.getGroupId(), message);
    }

}

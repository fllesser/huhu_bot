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
public class GroupMessageEvent extends MessageEvent {

    public GroupMessageEvent(MessageResp messageResp, Long groupId, String anonymous) {
        GroupMessageEvent groupMessageEvent = new GroupMessageEvent();
        BeanUtils.copyProperties(messageResp, groupMessageEvent);
        this.groupId = groupId;
        this.anonymous = anonymous;
    }
    private Long groupId;
    private String anonymous;

}

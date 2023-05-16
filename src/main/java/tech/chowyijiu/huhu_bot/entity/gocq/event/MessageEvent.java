package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.MessageResp;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Sender;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */

@Getter
@Setter
public class MessageEvent extends Event {

    private final String postType = PostTypeEnum.message.name();
    private String messageType;
    private Long selfId;
    private String subType;
    private Long userId;
    private String messageId;
    private String rawMessage;

    private Sender sender;
    private boolean toMe;


    public static MessageEvent respToEvent(MessageResp messageResp) {
        if (Objects.equals(messageResp.getMessageType(), MessageTypeEnum.private_.name())) {
            return new PrivateMessageEvent(messageResp);
        } else if (Objects.equals(messageResp.getMessageType(), MessageTypeEnum.group.name())) {
            return new GroupMessageEvent(messageResp, messageResp.getGroupId(), messageResp.getAnonymous());
        }
        MessageEvent messageEvent = new GroupMessageEvent();
        BeanUtils.copyProperties(messageResp, messageEvent);
        return messageEvent;
    }

}

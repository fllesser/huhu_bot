package tech.chowyijiu.huhu_bot.entity.gocq.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString(callSuper = true)
@NoArgsConstructor
public class MessageEvent extends Event {

    private final String postType = PostTypeEnum.message.name();

    private String subType;
    private Long userId;
    private String messageType;
    private String messageId;
    private String message;
    private String rawMessage;

    private Integer font;
    private Sender sender;
    private boolean toMe;


    public static MessageEvent respToEvent(MessageResp messageResp) {
        if (Objects.equals(messageResp.getMessageType(), MessageTypeEnum.private_.getType())) {
            return new PrivateMessageEvent(messageResp);
        } else if (Objects.equals(messageResp.getMessageType(), MessageTypeEnum.group.getType())) {
            return new GroupMessageEvent(messageResp);
        }
        MessageEvent messageEvent = new MessageEvent();
        BeanUtils.copyProperties(messageResp, messageEvent);
        return messageEvent;
    }

}

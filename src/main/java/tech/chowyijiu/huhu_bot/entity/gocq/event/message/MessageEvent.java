package tech.chowyijiu.huhu_bot.entity.gocq.event.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.response.WsResp;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Sender;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class MessageEvent extends Event {

    private final String postType = PostTypeEnum.message.name();

    private String subType;
    private Long userId;
    private String messageType;
    private String messageId;
    private String message;
    private String rawMessage;

    private Integer font;
    private Sender sender;


    public static MessageEvent respToEvent(WsResp wsResp) {
        if (Objects.equals(wsResp.getMessageType(), MessageTypeEnum.private_.getType())) {
            return new PrivateMessageEvent(wsResp);
        } else if (Objects.equals(wsResp.getMessageType(), MessageTypeEnum.group.getType())) {
            return new GroupMessageEvent(wsResp);
        }
        return null;
    }

}

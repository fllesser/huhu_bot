package tech.chowyijiu.huhu_bot.event.message;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.Message;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Sender;
import tech.chowyijiu.huhu_bot.event.Event;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 16/5/2023
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageEvent extends Event {

    private String postType; //message message_sent
    private String subType;
    private Long userId;
    private String messageType;
    private Integer messageId;
    private String message;
    private String rawMessage;

    private Integer font; //0
    private Sender sender;

    private transient Message msg;
    private transient String commandArgs;

    public static MessageEvent jsonToMessageEvent(JSONObject jsonObject) {
        String messageType = jsonObject.getString("message_type");
        MessageEvent event;
        if (Objects.equals(messageType, MessageTypeEnum.private_.getType())) {
            event = jsonObject.toJavaObject(PrivateMessageEvent.class);
            event.msg = Message.toMessage(event.message);
        } else {
            event = jsonObject.toJavaObject(GroupMessageEvent.class);
            event.msg = Message.toMessage(event.message);
            if (event.msg.isToMe(event.getSelfId())) {
                ((GroupMessageEvent) event).setToMe(true);
            }
        }
        return event;
    }

}

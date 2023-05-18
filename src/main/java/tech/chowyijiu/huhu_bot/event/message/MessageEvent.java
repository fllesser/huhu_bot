package tech.chowyijiu.huhu_bot.event.message;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
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

    private final String postType = PostTypeEnum.message.name();

    private String subType;
    private Long userId;
    private String messageType;
    private String messageId;
    private String message;
    private String rawMessage;

    private Integer font;
    private Sender sender;

    public static MessageEvent jsonToMessageEvent(JSONObject jsonObject) {
        String messageType = jsonObject.getString("message_type");
        if (Objects.equals(messageType, MessageTypeEnum.private_.getType())) {
            return jsonObject.toJavaObject(PrivateMessageEvent.class);
        } else {
            GroupMessageEvent event = jsonObject.toJavaObject(GroupMessageEvent.class);
            MessageSegment.CqCode cqCode = MessageSegment.toCqCode(event.getMessage());
            if (cqCode != null && Objects.equals(cqCode.getType(), CqTypeEnum.at)
                    && Objects.equals(cqCode.getParams().get("qq"), event.getSelfId().toString())) {
                event.setToMe(true);
            }
            return event;
        }
    }

}

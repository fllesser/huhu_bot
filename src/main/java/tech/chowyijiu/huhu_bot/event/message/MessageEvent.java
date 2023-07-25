package tech.chowyijiu.huhu_bot.event.message;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.arr_message.Message;
import tech.chowyijiu.huhu_bot.entity.response.Sender;
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
    private String subType; //group, public
    private Long userId;
    private String messageType;
    private Integer messageId;
    //注意这里gocq要设置成字符串格式消息, 就是raw_message
    //后续看要不要换成数组消息
    private Message message;
    private String rawMessage;

    private Integer font; //0
    private Sender sender;

    //@JsonIgnore
    //private Message msg;

    //去除匹配的命令, 保留剩余的字符串,并去掉头尾空格, 注意不会去除at
    @JsonIgnore
    private String commandArgs;

    public static MessageEvent build(JSONObject jsonObject) {
        String messageType = jsonObject.getString("message_type");
        MessageEvent event;
        if (Objects.equals(messageType, MessageTypeEnum.private_.getType())) {
            event = jsonObject.toJavaObject(PrivateMessageEvent.class);
        } else {
            event = jsonObject.toJavaObject(GroupMessageEvent.class);
        }
        return event;
    }


}

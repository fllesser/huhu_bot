package tech.chowyijiu.huhubot.core.event.message;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
import tech.chowyijiu.huhubot.core.entity.response.Sender;
import tech.chowyijiu.huhubot.core.event.Event;

/**
 * @author elastic chow
 * @date 16/5/2023
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageEvent extends Event {

    @JsonProperty("post_type")
    private String postType; //message message_sent
    @JsonProperty("sub_type")
    private String subType; //group, public
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("message_id")
    private Integer messageId;
    //注意gocq, config.yml中要设置数组array消息格式
    @JsonProperty("message")
    private Message message;
    @JsonProperty("raw_message")
    private String rawMessage;
    @JsonProperty("font")
    private Integer font; //0
    @JsonProperty("sender")
    private Sender sender;

    //去除匹配的命令, 保留剩余的字符串,并去掉头尾空格, 注意不会去除at
    @JsonIgnore
    private String commandArgs;

    public static MessageEvent build(final JSONObject jsonObject) {
        String messageType = jsonObject.getString("message_type");
        MessageEvent event;
        if ("private".equals(messageType)) {
            event = jsonObject.toJavaObject(PrivateMessageEvent.class);
        } else {
            event = jsonObject.toJavaObject(GroupMessageEvent.class);
        }
        return event;
    }


}

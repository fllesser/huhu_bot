package tech.flless.huhubot.adapters.onebot.v11.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import tech.flless.huhubot.adapters.onebot.v11.entity.arr_message.Message;

/**
 * @author flless
 * @date 9/9/2023
 */
@Data
public class MessageInfo {

    private boolean group;      //是否是群消息
    @JsonProperty("group_id")
    private Long groupId;       //是群消息时的群号(否则不存在此字段)
    @JsonProperty("message_id")
    private Integer messageId;  //消息id
    @JsonProperty("real_id")
    private Integer realId;     //消息真实id
    @JsonProperty("message_type")
    private String messageType; //群消息时为group, 私聊消息为private
    private Sender sender;      //发送者
    private Integer time;       //发送时间
    private Message message;    //消息内容
}

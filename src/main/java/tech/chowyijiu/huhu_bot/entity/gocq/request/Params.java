package tech.chowyijiu.huhu_bot.entity.gocq.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;

import java.util.Collection;

/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Data
@Accessors(chain = true)
public class Params {

    @JSONField(name = "message_type")
    private String messageType;
    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "group_id")
    private Long groupId;
    private Object message;
    private Collection messages;
    @JSONField(name = "auto_escape")
    private boolean autoEscape;

    public static Params privateMessageParams() {
        return new Params().setMessageType(MessageTypeEnum.private_.getType());
    }

    public static Params groupMessageParams() {
        return new Params().setMessageType(MessageTypeEnum.group.getType());
    }

}

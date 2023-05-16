package tech.chowyijiu.huhu_bot.utils;

import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 15/5/2023
 */
public class TypeUtil {

    public static boolean isPrivateMessage(Message message) {
        return Objects.equals(message.getMessageType(), MessageTypeEnum.private_.getType())
                || message.getGroupId() == null;
    }

    public static boolean isGroupMessage(Message message) {
        return Objects.equals(message.getMessageType(), MessageTypeEnum.group.getType())
                || message.getGroupId() != null;
    }
}

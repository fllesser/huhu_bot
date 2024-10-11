package tech.flless.huhubot.plugins.personal;

import lombok.extern.slf4j.Slf4j;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.RuleCheck;
import tech.flless.huhubot.core.rule.RuleEnum;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.Message;
import tech.flless.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import tech.flless.huhubot.adapters.onebot.v11.event.message.PrivateMessageEvent;

/**
 * @author elastic chow
 * @date 21/5/2023
 */
@Slf4j
@BotPlugin(name = "huhubot-plugin-seveneight")
@SuppressWarnings("unused")
public class PersonalPlugin {

    @RuleCheck(rule = RuleEnum.temp_session)
    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(PrivateMessageEvent event) {
        Message message = Message.text("[bot]").append(MessageSegment.at(event.getUserId()))
                .append("请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了");
        event.getBot().sendGroupMessage(event.getSender().getGroupId(), message);
    }


}

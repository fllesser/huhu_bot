package tech.chowyijiu.huhubot.plugins.personal;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.CoolDown;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.event.message.PrivateMessageEvent;

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

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "文字转语音", commands = {"tts", "文字转语音"})
    public void tts(GroupMessageEvent event) {
        event.sendMessage(MessageSegment.tts(event.getCommandArgs()));
    }


    @CoolDown(seconds = 120)
    @MessageHandler(name = "遥遥领先", keywords = {"遥遥领先", "yylx"})
    public void yaoYaoLingXian(MessageEvent event) {
        event.sendMessage(MessageSegment.record("file:///home/chow/oswald/huhubot/record/yaoyaolingxian.mp3", 0));
    }

}

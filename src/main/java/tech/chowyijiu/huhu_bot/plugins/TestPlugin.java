package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
import tech.chowyijiu.huhu_bot.utils.MessageSegment;
import tech.chowyijiu.huhu_bot.utils.TypeUtil;
import tech.chowyijiu.huhu_bot.ws.Server;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @MessageHandler(name = "测试消息", command = {"测试", "test"})
    public void test1(WebSocketSession session, Message message) {
        if (TypeUtil.isPrivateMessage(message)) {
            Server.sendPrivateMessage(session, message.getSender().getUserId(), "测试消息" ,false);
        } else if (TypeUtil.isGroupMessage(message)) {
            Server.sendGroupMessage(session, message.getGroupId(), "测试消息", false);
        }
    }

    @MessageHandler(type = MessageTypeEnum.group, name = "群聊测试", command = {"群聊测试"})
    public void test2(WebSocketSession session, Message message) {
        Server.sendGroupMessage(session, message.getGroupId(), "测试群聊", true);
    }

    @MessageHandler(type = MessageTypeEnum.private_, name = "私聊测试", command = {"私聊测试"})
    public void test3(final WebSocketSession session, Message message) {
        Server.sendPrivateMessage(session, message.getUserId(), "测试私聊", true);
    }

    @NoticeHandler(type = NoticeTypeEnum.group_card, name = "群名片变更推送")
    public void test4(final WebSocketSession session, Message message) {
        log.info("群名片变更");
    }


    @NoticeHandler(type = NoticeTypeEnum.notify, subType = SubTypeEnum.poke, name = "戳一戳")
    public void test5(final WebSocketSession session, Message message) {
        log.info("戳一戳 message: {}", message);
        String poke = MessageSegment.poke(message.getUserId());
        if (TypeUtil.isPrivateMessage(message)) {
            Server.sendPrivateMessage(session, message.getSender().getUserId(), poke ,false);
        } else if (TypeUtil.isGroupMessage(message)) {
            Server.sendGroupMessage(session, message.getGroupId(), poke , false);
        }
    }
}

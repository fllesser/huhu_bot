package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.constant.NoticeTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
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
        Server.sendPrivateMessage(session, 1942422015L, "测试所有消息", true);
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
}

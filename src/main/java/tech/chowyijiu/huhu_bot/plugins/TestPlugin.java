package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
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
        Server.sendGroupMessage(session, message.getGroupId(), "测试消息", false);
    }

    @MessageHandler(name = "群聊测试", command = {"群聊测试"})
    public void test2(WebSocketSession session, Message message) {
        Server.sendGroupMessage(session, message.getGroupId(), "测试群聊", false);
    }

    @MessageHandler(name = "私聊测试", command = {"私聊测试"})
    public void test3(final WebSocketSession session, Message message) {
        Server.sendPrivateMessage(session, message.getUserId(), "测试私聊", false);
    }
}

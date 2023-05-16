package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.notice.NoticeHandler;
import tech.chowyijiu.huhu_bot.constant.SubTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.NoticeEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.utils.MessageSegment;
import tech.chowyijiu.huhu_bot.ws.Server;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @MessageHandler(name = "测试消息", command = {"测试", "test"})
    public void test1(WebSocketSession session, MessageEvent event) {
        if (event instanceof PrivateMessageEvent) {
            Server.sendPrivateMessage(session, event.getSender().getUserId(), "测试消息" ,false);
        } else if (event instanceof GroupMessageEvent) {
            Server.sendGroupMessage(session, ((GroupMessageEvent) event).getGroupId(), "测试消息", false);
        }

    }

    @MessageHandler(name = "群聊测试", command = {"echo"})
    public void test2(WebSocketSession session, GroupMessageEvent event) {
        Server.sendGroupMessage(session, event.getGroupId(), "测试群聊", true);
    }

    @MessageHandler(name = "私聊测试", command = {"echo"})
    public void test3(final WebSocketSession session, PrivateMessageEvent event) {
        Server.sendPrivateMessage(session, event.getUserId(), "测试私聊", true);
    }

    @NoticeHandler(name = "群名片变更推送", subType = SubTypeEnum.title)
    public void test4(final WebSocketSession session, NoticeEvent event) {
        log.info("群名片变更");
    }


    @NoticeHandler(name = "戳一戳", subType = SubTypeEnum.poke)
    public void test5(final WebSocketSession session, NoticeEvent event) {
        log.info("戳一戳 message: {}", event);
        String poke = MessageSegment.poke(event.getUserId());
    }
}

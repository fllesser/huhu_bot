package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.entity.gocq.event.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.response.Message;
import tech.chowyijiu.huhu_bot.utils.MessageSegment;
import tech.chowyijiu.huhu_bot.ws.Server;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@BotPlugin(name = "堡垒之夜")
public class FortnitePlugin {


    @MessageHandler(type = MessageTypeEnum.group, name = "每日vb图", command = {"vb", "VB"})
    public void vb(WebSocketSession session, Message message) {
        Server.sendGroupMessage(session, message.getGroupId(), "vb图", true);
    }

    @MessageHandler(name = "商城", command = {"商城"})
    public void shop(WebSocketSession session, MessageEvent event) {
        String cq = MessageSegment.image("https://cdn.dingpanbao.cn/blzy/shop.png");
        if (event instanceof PrivateMessageEvent) {
            Server.sendPrivateMessage(session, event.getUserId(), cq ,false);
        } else if (event instanceof GroupMessageEvent) {
            Server.sendGroupMessage(session, ((GroupMessageEvent) event).getGroupId(), cq, false);
        }
    }
}

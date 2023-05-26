package tech.chowyijiu.huhu_bot.plugins.fortnite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.message.MessageSegment;
import tech.chowyijiu.huhu_bot.event.message.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;
import tech.chowyijiu.huhu_bot.ws.Server;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
//@BotPlugin(name = "堡垒之夜")
public class FortnitePlugin {


    private final String shop = MessageSegment.image("https://cdn.dingpanbao.cn/blzy/shop.png") + "";

    @MessageHandler(name = "每日vb图", commands = {"vb", "VB"}, priority = 4)
    public void vb(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "vb图", true);
    }

    @MessageHandler(name = "商城", commands = {"商城", "shop"}, priority = 1, cutdown = 60)
    public void shop(Bot bot, MessageEvent event) {
        bot.sendMessage(event, shop, false);
    }

    //@MessageHandler(name = "商城2", commands = {"shop2"}, priority = 0)
    //public void shop2(Bot bot, GroupMessageEvent event) {
    //    String message = "今日商城" + MessageSegment.image("https://cdn.dingpanbao.cn/blzy/shop.png") + "由商城2发出";
    //    bot.sendMessage(event, message, false);
    //}
    //
    //@MessageHandler(name = "商城3", keywords = {"商城"}, priority = 3)
    //public void shop3(Bot bot, GroupMessageEvent event) {
    //    MessageSegment image = MessageSegment.image("file:///home/chow/shop.png");
    //    bot.sendMessage(event, image + "", false);
    //}

    @Scheduled(cron = "0 1 8 * * * ")
    public void scheduleShop() {
        Server.getBots().forEach(bot -> bot.sendPrivateMessage(1942422015L, shop + "", false));
    }

}

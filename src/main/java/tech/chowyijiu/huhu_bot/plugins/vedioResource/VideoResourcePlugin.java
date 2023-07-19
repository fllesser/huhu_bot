package tech.chowyijiu.huhu_bot.plugins.vedioResource;

import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe.GitCafeReq;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@BotPlugin
public class VideoResourcePlugin {

    @MessageHandler(name = "阿里云盘资源搜索", commands = {"search"})
    public void search(Bot bot, MessageEvent event) {
        String data = StringUtil.hasLengthReturn(event.getCommandArgs(), GitCafeReq::get);
        if (!StringUtil.hasLength(data)) event.finish("查询失败" + data);
        bot.sendMessage(event, data, false);
    }
}

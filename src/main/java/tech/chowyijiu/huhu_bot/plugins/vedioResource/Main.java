package tech.chowyijiu.huhu_bot.plugins.vedioResource;

import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe.GitCafeReq;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.List;

/**
 * @author elastic chow
 * @date 17/7/2023
 */
@BotPlugin
public class Main {

    @MessageHandler(name = "阿里云盘资源搜索", commands = {"search"})
    public void search(Bot bot, MessageEvent event) {
        List<WillSendData> list = StringUtil.hasLengthReturn(event.getCommandArgs(), GitCafeReq::get);
        if (list == null || list.isEmpty()) {
            bot.sendMessage(event, "查询失败, 可能暂无此关键词资源", false);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("共查询到").append(list.size()).append("个资源");
        list.forEach(data -> sb.append("\n").append(data.getTitle()).append(data.getUrl()));
        bot.sendMessage(event, sb.toString(), false);
    }
}

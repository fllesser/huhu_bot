package tech.chowyijiu.huhu_bot.plugins.vedioResource;

import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe.GitCafeReq;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.hdhive.HdhiveReq;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.util.Objects;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@BotPlugin
public class VideoResourcePlugin {


    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledCheck() {
        Objects.requireNonNull(Server.getBot(1942422015L))
                .sendGroupMessage(BotConfig.testGroup, AliYunDriver.dailyCheck(), false);
    }

    @MessageHandler(name = "阿里云盘资源搜索 gitcafe ", commands = {".s"})
    public void search1(Bot bot, MessageEvent event) {
        String data = StringUtil.hasLengthReturn(event.getCommandArgs(), GitCafeReq::get);
        if (!StringUtil.hasLength(data)) event.finish("查询失败" + data);
        bot.sendMessage(event, data, false);
    }

    @MessageHandler(name = "阿里云盘资源搜索 hdhive", commands = {".ds"})
    public void search2(Bot bot, MessageEvent event) {
        String data = StringUtil.hasLengthReturn(event.getCommandArgs(), HdhiveReq::get1);
        if (!StringUtil.hasLength(data)) event.finish("查询失败" + data);
        bot.sendMessage(event, data, false);
    }
}

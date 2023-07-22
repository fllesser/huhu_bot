package tech.chowyijiu.huhu_bot.plugins.resource_search;

import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.core.rule.RuleEnum;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.plugins.resource_search.cache_.ResourceData;
import tech.chowyijiu.huhu_bot.plugins.resource_search.cache_.ResourceUtil;
import tech.chowyijiu.huhu_bot.plugins.resource_search.gitcafe.GitCafeReq;
import tech.chowyijiu.huhu_bot.plugins.resource_search.hdhive.HdhiveReq;
import tech.chowyijiu.huhu_bot.utils.StringUtil;
import tech.chowyijiu.huhu_bot.ws.Bot;
import tech.chowyijiu.huhu_bot.ws.Server;

import java.util.List;
import java.util.Objects;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@BotPlugin
public class ResourceSearchPlugin {


    @Scheduled(cron = "0 0 12 * * *")
    public void scheduledCheck() {
        Objects.requireNonNull(Server.getBot(BotConfig.superUsers.get(0)))
                .sendGroupMessage(BotConfig.testGroup,
                        "阿里云盘签到结果: " + AliYunDriver.dailySignIn(), false);
        //清除搜索
        ResourceUtil.clear();
    }

    @MessageHandler(name = "阿里云盘资源搜索 gitcafe ", commands = {".s"})
    public void search1(Bot bot, MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLengthReturn(event.getCommandArgs(), GitCafeReq::get);
        bot.sendMessage(event, ResourceUtil.buildString(dataList), false);
    }

    @MessageHandler(name = "阿里云盘资源搜索 hdhive", commands = {".ds"})
    public void search2(Bot bot, MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLengthReturn(event.getCommandArgs(), HdhiveReq::get1);
        bot.sendMessage(event, ResourceUtil.buildString(dataList), false);
    }

    /**
     * .save/保存 搜索时的关键词 序号
     */
    @MessageHandler(name = "转存到阿里云盘", commands = {".save"},  priority = 1,
            rule = RuleEnum.superuser, block = true)
    public void save(Bot bot, MessageEvent event) {
        String commandArgs = event.getCommandArgs();
        String[] keyNo = commandArgs.split(" ");
        if (keyNo.length != 2) {
            bot.sendMessage(event, "参数错误, 请参考 .save 搜索时的关键词 序号", false);
            return;
        }
        int index = 0;
        if (StringUtil.isDigit(keyNo[1])) index = Integer.parseInt(keyNo[1]) - 1;
        if (index < 0) index = 0;
        ResourceData data = ResourceUtil.get(keyNo[0], index);
        if (data != null) {
            boolean success = AliYunDriver.fileCopy(data.getShareId());
            String willSend = "转存[" + (index + 1) + "]" + data.getName();
            if (success) {
                willSend += "成功\n删除Openwrt阿里云盘缓存" + (OpenwrtReq.invalidateCache() ? "成功" : "失败");
            } else {
                willSend += "失败";
            }
            bot.sendMessage(event, willSend, false);
        }

    }

    @MessageHandler(name = "openwrt aliyundrive invalidate cache",
            keywords = {".ic", "删除缓存"}, rule = RuleEnum.superuser)
    public void _1(Bot bot, MessageEvent event) {
        boolean ok = OpenwrtReq.invalidateCache();
        bot.sendMessage(event, "删除Openwrt阿里云盘缓存" + (ok ? "成功" : "失败"), false);
    }
}

package tech.chowyijiu.huhubot.plugins.resource_search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import tech.chowyijiu.huhubot.annotation.BotPlugin;
import tech.chowyijiu.huhubot.annotation.MessageHandler;
import tech.chowyijiu.huhubot.config.BotConfig;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.event.message.MessageEvent;
import tech.chowyijiu.huhubot.plugins.resource_search.cache_.ResourceData;
import tech.chowyijiu.huhubot.plugins.resource_search.cache_.ResourceUtil;
import tech.chowyijiu.huhubot.plugins.resource_search.gitcafe.GitCafeReq;
import tech.chowyijiu.huhubot.plugins.resource_search.hdhive.HdhiveReq;
import tech.chowyijiu.huhubot.utils.StringUtil;
import tech.chowyijiu.huhubot.utils.xiaoai.XiaoAIUtil;
import tech.chowyijiu.huhubot.ws.Bot;
import tech.chowyijiu.huhubot.ws.Server;

import java.util.List;
import java.util.Objects;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@Slf4j
@BotPlugin
public class ResourceSearchPlugin {


    @Scheduled(cron = "0 0 10 * * *")
    public void scheduledCheck() {
        String result = AliYunApi.signInList();
        Objects.requireNonNull(Server.getBot(BotConfig.superUsers.get(0)))
                .sendGroupMessage(BotConfig.testGroup, result);
        XiaoAIUtil.tts(result);
        //清除搜索
        ResourceUtil.clear();
    }

    @MessageHandler(name = "阿里云盘手动签到", commands = "alisign", rule = RuleEnum.superuser)
    public void aliSignIn(Bot bot, MessageEvent event) {
        String result = AliYunApi.signInList();
        bot.sendMessage(event, result);
        XiaoAIUtil.tts(result);
    }

    @MessageHandler(name = "阿里云盘资源搜索 GITCAFE", commands = {".s"})
    public void search1(Bot bot, MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), GitCafeReq::get);
        bot.sendMessage(event, ResourceUtil.buildString(dataList));
    }

    @MessageHandler(name = "阿里云盘资源搜索 HDHIVE", commands = {".ds"})
    public void search2(Bot bot, MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), HdhiveReq::get1);
        bot.sendMessage(event, ResourceUtil.buildString(dataList));
    }

    /**
     * .save/保存 搜索时的关键词 序号
     */
    @MessageHandler(name = "转存到阿里云盘", commands = {".save"}, priority = 1,
            rule = RuleEnum.superuser, block = true)
    public void save(Bot bot, MessageEvent event) {
        String no = event.getCommandArgs();
        if (!StringUtil.isDigit(no)) {
            bot.sendMessage(event, "参数应为数字");
            return;
        }
        int index = Integer.parseInt(no);
        ResourceData data = ResourceUtil.get(index);
        if (data != null) {
            boolean success = AliYunApi.fileCopy(data.getShareId());
            String willSend = "转存[" + index + "]" + data.getName();
            if (success) {
                willSend += "成功\n删除Openwrt阿里云盘缓存" + (OpenwrtReq.invalidateCache() ? "成功" : "失败");
            } else {
                willSend += "失败, 分享者取消分享, 或被风控";
            }
            bot.sendMessage(event, willSend);
        }

    }

    @MessageHandler(name = "search in cache", commands = {".cache"}, rule = RuleEnum.superuser)
    public void searchInCache(Bot bot, MessageEvent event) {
        String keyword = event.getCommandArgs();
        String cacheData = StringUtil.hasLength(keyword, ResourceUtil::getByKeyWord);
        String willSend = StringUtil.hasLength(cacheData) ? "从缓存中搜索到以下资源" + cacheData :"缓存中没有相关资源";
        bot.sendMessage(event, willSend);
    }
}

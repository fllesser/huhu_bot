package com.github.huhubot.plugins.resource_search;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.core.annotation.BotPlugin;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.core.rule.RuleEnum;
import com.github.huhubot.plugins.resource_search.cache_.ResourceData;
import com.github.huhubot.plugins.resource_search.cache_.ResourceUtil;
import com.github.huhubot.plugins.resource_search.gitcafe.GitCafeClient;
import com.github.huhubot.plugins.resource_search.hdhive.HdhiveClient;
import com.github.huhubot.utils.StringUtil;


import java.util.List;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@Slf4j
@BotPlugin("Resource Search")
@SuppressWarnings("unused")
public class ResourceSearchPlugin {

    @Resource
    private GitCafeClient gitCafeClient;
    @Resource
    private HdhiveClient hdhiveClient;
//    @Resource
//    private OpenwrtClient openwrtClient;

    @Resource
    private AliYunDriverClient aliYunDriverClient;

//    @Async
//    @Scheduled(cron = "0 30 10 * * *")
//    public void scheduledCheck() {
//        String result = aliYunDriverClient.signInList();
//        Objects.requireNonNull(BotContainer.getBot(BotConfig.superUsers.get(0)))
//                .sendGroupMessage(BotConfig.testGroup, result);
//        //XiaoAIUtil.tts(result);
//        //清除搜索缓存
//        ResourceUtil.clear();
//    }

    //@RuleCheck(rule = RuleEnum.superuser)
    //@MessageHandler(name = "阿里云盘手动签到", commands = "alisign")
    @Deprecated
    public void aliSignIn(MessageEvent event) {
        String result;
        try {
            result = aliYunDriverClient.signInList();
        } catch (RuntimeException e) {
            result = "阿里云盘签到失败, refresh token 可能过期";
        }
        event.reply(result);
        //XiaoAIUtil.tts(result);
    }

    @MessageHandler(name = "Gitcafe Api", commands = {".s"})
    public void gitCafeSearch(MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), gitCafeClient::get);
        event.reply(ResourceUtil.buildString(dataList));
    }


    @MessageHandler(name = "Hdhive Api", commands = {".ds"})
    public void hdhiveSearch(MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), hdhiveClient::get);
        event.reply(ResourceUtil.buildString(dataList));
    }



    /**
     * .save/保存 搜索时的关键词 序号
     */
//    @RuleCheck(rule = RuleEnum.superuser)
//    @MessageHandler(name = "转存到阿里云盘", commands = {".save"}, priority = 1, block = true)
//    public void save(MessageEvent event) {
//        String no = event.getCommandArgs();
//        if (!StringUtil.isDigit(no)) {
//            event.reply("参数应为数字");
//            return;
//        }
//        int index = Integer.parseInt(no);
//        ResourceData data = ResourceUtil.get(index);
//        if (data != null) {
//            boolean success = false;
//            String willSend = "转存[" + index + "]" + data.getName();
//            try {
//                success = aliYunDriverClient.fileCopy(data.getShareId());
//            } catch (Exception e) {
//                willSend = "refresh token expired, " + willSend;
//            }
//            if (success) {
//                willSend += "成功\n删除Openwrt阿里云盘缓存" + (openwrtClient.invalidateCache() ? "成功" : "失败");
//            } else {
//                willSend += "失败, 分享者取消分享, 或被风控";
//            }
//            event.reply(willSend);
//        }
//
//    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "搜索缓存", commands = {".cache"})
    public void searchInCache(MessageEvent event) {
        String keyword = event.getCommandArgs();
        String cacheData = StringUtil.hasLength(keyword, ResourceUtil::getByKeyWord);
        String willSend = StringUtil.hasLength(cacheData) ? "从缓存中搜索到以下资源" + cacheData :"缓存中没有相关资源";
        event.reply(willSend);
    }
}

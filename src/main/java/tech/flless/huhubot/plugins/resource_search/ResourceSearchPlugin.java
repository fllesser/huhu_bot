package tech.flless.huhubot.plugins.resource_search;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import tech.flless.huhubot.adapters.onebot.v11.bot.BotContainer;
import tech.flless.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import tech.flless.huhubot.config.BotConfig;
import tech.flless.huhubot.core.annotation.BotPlugin;
import tech.flless.huhubot.core.annotation.MessageHandler;
import tech.flless.huhubot.core.annotation.RuleCheck;
import tech.flless.huhubot.core.rule.RuleEnum;
import tech.flless.huhubot.plugins.resource_search.cache_.ResourceData;
import tech.flless.huhubot.plugins.resource_search.cache_.ResourceUtil;
import tech.flless.huhubot.plugins.resource_search.gitcafe.GitCafeClient;
import tech.flless.huhubot.plugins.resource_search.hdhive.HdhiveClient;
import tech.flless.huhubot.utils.StringUtil;
import tech.flless.huhubot.utils.xiaoai.XiaoAIUtil;

import java.util.List;
import java.util.Objects;


/**
 * @author elastic chow
 * @date 17/7/2023
 */
@Slf4j
@BotPlugin("huhubot-plugin-resourcesearch")
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
        event.replyMessage(result);
        XiaoAIUtil.tts(result);
    }

    @MessageHandler(name = "GITCAFE", commands = {".s"})
    public void gitCafeSearch(MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), gitCafeClient::get);
        event.replyMessage(ResourceUtil.buildString(dataList));
    }


    @MessageHandler(name = "HDHIVE", commands = {".ds"})
    public void hdhiveSearch(MessageEvent event) {
        List<ResourceData> dataList = StringUtil.hasLength(event.getCommandArgs(), hdhiveClient::get);
        event.replyMessage(ResourceUtil.buildString(dataList));
    }



    /**
     * .save/保存 搜索时的关键词 序号
     */
//    @RuleCheck(rule = RuleEnum.superuser)
//    @MessageHandler(name = "转存到阿里云盘", commands = {".save"}, priority = 1, block = true)
//    public void save(MessageEvent event) {
//        String no = event.getCommandArgs();
//        if (!StringUtil.isDigit(no)) {
//            event.replyMessage("参数应为数字");
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
//            event.replyMessage(willSend);
//        }
//
//    }

    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "search in cache", commands = {".cache"})
    public void searchInCache(MessageEvent event) {
        String keyword = event.getCommandArgs();
        String cacheData = StringUtil.hasLength(keyword, ResourceUtil::getByKeyWord);
        String willSend = StringUtil.hasLength(cacheData) ? "从缓存中搜索到以下资源" + cacheData :"缓存中没有相关资源";
        event.replyMessage(willSend);
    }
}

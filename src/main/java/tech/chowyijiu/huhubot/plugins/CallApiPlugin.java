package tech.chowyijiu.huhubot.plugins;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.constant.GocqAction;
import tech.chowyijiu.huhubot.core.entity.arr_message.ForwardMessage;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.ws.Bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@SuppressWarnings("unused")
@Slf4j
@BotPlugin(name = "huhubot-plugin-gocqapi")
public class CallApiPlugin {


    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "callApi", commands = "api")
    public void apiTest(MessageEvent event) {
        //[key:value,key:value]
        Bot bot = event.getBot();
        String[] args = event.getCommandArgs().split(" ");
        GocqAction action = null;
        try {
            action = GocqAction.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            bot.sendMessage(event, "没有这个API, 或huhubot暂未支持");
        }
        String[] keyValue = new String[args.length - 1];
        System.arraycopy(args, 1, keyValue, 0, args.length - 1);
        Map<String, Object> map = Arrays.stream(keyValue)
                .map(kv -> kv.split(":"))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
        assert action != null;
        if (!action.isHasResp()) {
            bot.callApi(action, map);
            bot.sendMessage(event, action.getRemark() + "已发送ws请求, 该api无响应数据");
            return;
        }
        long start = System.currentTimeMillis();
        String resp = bot.callApiWaitResp(action, map);
        long end = System.currentTimeMillis();
        String costTime = "time-consuming: " + (end - start) + "ms";
        if (resp.startsWith("{")) {
            bot.sendMessage(event, resp + "\n" + costTime);
        } else if (resp.startsWith("[")) {
            List<Object> messages = new ArrayList<>();
            messages.add(costTime);
            messages.add(MessageSegment.image("https://pic2.zhimg.com/80/v2-d9a3bd17467c3b51e35c95cbbfe6a755_1440w.webp"));
            messages.addAll(JSONArray.parseArray(resp, String.class).stream().limit(98).toList());
            List<ForwardMessage> nodes = ForwardMessage.quickBuild("OneBotV11Adapter", event.getUserId(), messages);
            bot.sendForwardMsg(event, nodes);
        }
    }

}

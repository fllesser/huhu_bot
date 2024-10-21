package com.github.huhubot.plugins;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.huhubot.adapters.onebot.v11.entity.message.Message;
import com.github.huhubot.adapters.onebot.v11.entity.message.MessageSegment;
import com.github.huhubot.core.exception.FinishedException;
import lombok.extern.slf4j.Slf4j;
import com.github.huhubot.adapters.onebot.v11.bot.Bot;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.core.annotation.BotPlugin;
import com.github.huhubot.core.annotation.MessageHandler;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.adapters.onebot.v11.constant.OnebotAction;
import com.github.huhubot.core.rule.RuleEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@SuppressWarnings("unused")
@Slf4j
@BotPlugin(name = "CallApi")
public class CallApiPlugin {


    /**
     * ai get_group_info group_id:12312321 k:v ...
     *
     * @param event MessageEvent
     */
    @RuleCheck(rule = RuleEnum.superuser)
    @MessageHandler(name = "callApi", commands = "api")
    public void apiTest(MessageEvent event) {
        //[key:value,key:value]
        Bot bot = event.getBot();
        String[] args = event.getCommandArgs().split(" ");
        OnebotAction action = null;
        try {
            action = OnebotAction.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            event.finish("没有这个API, 或huhubot暂未支持");
        }
        String[] keyValue = new String[args.length - 1];
        System.arraycopy(args, 1, keyValue, 0, args.length - 1);
        Map<String, Object> map = Arrays.stream(keyValue)
                .map(kv -> kv.split(":"))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
        assert action != null;
        if (!action.isHasResp()) {
            bot.callApi(action, map);
            event.finish(action.getRemark() + "已发送ws请求, 该api无响应数据");
        }
        long start = System.currentTimeMillis();
        Object resp = bot.callApiWaitResp(action, map);
        long end = System.currentTimeMillis();
        String costTime = "time-consuming: " + (end - start) + "ms";
        if (resp != null) {
            if (resp instanceof JSONObject jsonObject) {
                StringBuilder willSend = new StringBuilder();
                jsonObject.forEach((k, v) -> willSend.append(k).append(":").append(v).append("\n"));
                event.reply(willSend + "\n" + costTime);
            } else if (resp instanceof JSONArray jsonArray) {
                Message message = new Message();
                message.append(MessageSegment.node("call-api-plugin", bot.getSelfId(), costTime));
                jsonArray.toJavaList(JSONObject.class).stream().limit(98)
                        .forEach(jsonObject -> {
                            StringBuilder willSend = new StringBuilder();
                            jsonObject.forEach((k, v) -> willSend.append(k).append(":").append(v).append("\n"));
                            message.append(MessageSegment.node("call-api-plugin", bot.getSelfId(), willSend.toString()));
                        });
                bot.sendForwardMsg(event, message);
            }
        } else {
            event.finish("onebotv11 实现端可能未支持该api");
        }

    }

}

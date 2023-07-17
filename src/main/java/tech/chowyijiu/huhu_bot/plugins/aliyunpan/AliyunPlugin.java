package tech.chowyijiu.huhu_bot.plugins.aliyunpan;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 17/7/2023
 */
@BotPlugin
public class AliyunPlugin {

    @MessageHandler(name = "阿里云盘资源搜索", commands = {"search"})
    public void search(Bot bot, MessageEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "search");
        map.put("from", "web");
        map.put("token", "14730e6c54e9bead73a1299c08896026e1c3e");
        map.put("keyword", event.getCommandArgs());
        String resp = HttpUtil.post("https://gitcafe.net/tool/alipaper/", map);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        if (!gitcafeResp.getSuccess()) event.finish("查询失败" + gitcafeResp.getError());
        StringBuilder sb = new StringBuilder();
        List<Data> dataList = gitcafeResp.getData();
        sb.append("共查询到").append(dataList.size()).append("个资源");
        dataList.forEach(data -> sb.append("\n").append(data.getAlititle())
                .append(" https://www.aliyundrive.com/s/").append(data.getAlikey()));
        bot.sendMessage(event, sb.toString(), false);
    }
}

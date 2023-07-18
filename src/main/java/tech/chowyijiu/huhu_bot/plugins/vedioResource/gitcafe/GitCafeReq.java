package tech.chowyijiu.huhu_bot.plugins.vedioResource.gitcafe;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.plugins.vedioResource.WillSendData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elastic chow
 * @date 18/7/2023
 */
public class GitCafeReq {

    public static List<WillSendData> get(String keyword) {
        Map<String, Object> map = Map.of("action", "search", "from", "web",
                "token","", "keyword", keyword);
        String resp = HttpUtil.post("https://gitcafe.net/tool/alipaper/", map);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        return gitcafeResp.getData().stream()
                .map(data -> WillSendData.builder()
                        .title(data.getAlititle())
                        .type("aliyun")
                        .url("https://www.aliyundrive.com/s/" + data.getAlikey())
                        .build())
                .collect(Collectors.toList());
    }
}

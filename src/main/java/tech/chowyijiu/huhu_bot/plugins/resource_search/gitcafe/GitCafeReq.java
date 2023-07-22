package tech.chowyijiu.huhu_bot.plugins.resource_search.gitcafe;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.plugins.resource_search.cache_.ResourceData;
import tech.chowyijiu.huhu_bot.plugins.resource_search.cache_.ResourceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 18/7/2023
 */
public class GitCafeReq {

    private static final String URL = "https://gitcafe.net/tool/alipaper/";
    private static final Map<String, Object> paramsMap = new HashMap<>(
            Map.of("action", "search", "from", "web", "token", ""));

    public static List<ResourceData> get(String keyword) {
        paramsMap.put("keyword", keyword);
        String resp = HttpUtil.post(URL, paramsMap);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        if (gitcafeResp.isSuccess()) {
            List<Data> dataList = gitcafeResp.getData();
            List<ResourceData> resourceDataList = dataList.stream()
                    .map(data -> new ResourceData(data.getAlititle(), data.getAlikey())).toList();
            ResourceUtil.put(keyword, resourceDataList);
        }
        return null;
    }
}

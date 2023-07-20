package tech.chowyijiu.huhu_bot.plugins.resource_search.gitcafe;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

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

    public static String get(String keyword) {
        paramsMap.put("keyword", keyword);
        String resp = HttpUtil.post(URL, paramsMap);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        StringBuilder sb = new StringBuilder();
        if (gitcafeResp.isSuccess()) {
            List<Data> list = gitcafeResp.getData();
            sb.append("共搜索到").append(list.size()).append("个资源");
            gitcafeResp.getData().forEach(data -> sb.append("\n[")
                    .append(data.getAlititle()).append("] ")
                    .append("https://www.aliyundrive.com/s/").append(data.getAlikey()));
        } else sb.append("gitcafe").append("查询失败, ").append(gitcafeResp.getError());
        return sb.toString();
    }
}

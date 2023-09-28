package tech.chowyijiu.huhubot.plugins.resource_search.gitcafe;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhubot.plugins.resource_search.cache_.ResourceData;

import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 18/7/2023
 */
@Component
public class GitCafeClient {

    private static final String URL = "https://gitcafe.net/tool/alipaper/";

    public List<ResourceData> get(String keyword) {
        Map<String, Object> paramsMap = Map.of("action", "search",
                "from", "web", "token", "", "keyword", keyword);
        String resp = HttpUtil.post(URL, paramsMap);
        GitcafeResp gitcafeResp = JSONObject.parseObject(resp, GitcafeResp.class);
        if (gitcafeResp.isSuccess()) {
            List<Data> dataList = gitcafeResp.getData();
            return dataList.stream()
                    .map(data -> new ResourceData(data.getAlititle(), data.getAlikey())).toList();
        }
        return null;
    }
}

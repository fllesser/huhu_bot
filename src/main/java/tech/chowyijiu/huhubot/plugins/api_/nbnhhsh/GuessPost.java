package tech.chowyijiu.huhubot.plugins.api_.nbnhhsh;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 26/7/2023
 */
public class GuessPost {

    private static final String url = "https://lab.magiconch.com/api/nbnhhsh/guess";
    private static final Map<String, String> headers = Map.of(
            "origin", "https://lab.magiconch.com",
            "referer", "https://lab.magiconch.com/nbnhhsh/",
            "user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36",
            "Content-Type", "application/json"
    );

    public static List<String> guess(String word) {
        Map<String, String> body = Map.of("text", word);
        HttpResponse response = HttpRequest.post(url).addHeaders(headers)
                .body(JSONObject.toJSONString(body)).execute();
        JSONArray jsonArray = JSONArray.parseArray(response.body());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        return jsonObject.getList("trans", String.class);
    }

}

package tech.chowyijiu.huhubot.plugins.api_.cat;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author flless
 * @date 13/9/2023
 */
public class RandomCat {

    public static final String url = "https://api.thecatapi.com/v1/images/search?limit=1";

    public static String get() {
        String respBody = HttpUtil.get(url);
        JSONArray jsonArray = JSONArray.parseArray(respBody);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        return jsonObject.getString("url");
    }
}

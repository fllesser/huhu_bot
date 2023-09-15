package tech.chowyijiu.huhubot.plugins.api_;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author flless
 * @date 15/9/2023
 */
public class HandFish {

    public static String imgUrl() {
        String url = "https://api.vvhan.com/api/moyu?type=json";
        String json = HttpUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(json);
        return jsonObject.getString("url");
    }
}

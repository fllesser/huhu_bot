package tech.chowyijiu.huhu_bot.plugins.vedioResource;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.config.BotConfig;

/**
 * @author elastic chow
 * @date 19/7/2023
 */
public class AliYunDriver {

    private static String refreshToken() {
        HttpResponse response = HttpRequest.post("https://auth.aliyundrive.com/v2/account/token")
                .header("Content-Type", "application/json")
                .body("{\"grant_type\":\"refresh_token\",\"refresh_token\":\"" + BotConfig.aliRefreshToken + "\"}")
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        return jsonObject.getString("access_token");
    }

    public static String dailyCheck() {
        HttpResponse httpResponse = HttpRequest.post("https://member.aliyundrive.com/v1/activity/sign_in_list")
                .header("Content-Type", "application/json")
                .header("Authorization", refreshToken())
                .body("{\"checkDate\": \"2020-12-16\"}")
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.body());
        Boolean success = jsonObject.getBoolean("success");
        if (success == null || !success) {
            return "签到失败, token可能过期";
        }
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray signInLogs = result.getJSONArray("signInLogs");
        JSONObject day_json = signInLogs.getJSONObject(signInLogs.size() - 2);
        String content;
        if (day_json.getBoolean("isReward")) {
            content = "本月累计签到" + result.getJSONObject("result").getInteger("signInCount") + "天,今日签到获得"
                    + day_json.getJSONObject("reward").getString("name") + day_json.getJSONObject("reward").getString("description");
        } else {
            content = "签到成功,今日未获得奖励";
        }
        return content;
    }
}

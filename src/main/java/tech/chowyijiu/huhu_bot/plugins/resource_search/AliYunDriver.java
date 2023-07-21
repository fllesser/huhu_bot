package tech.chowyijiu.huhu_bot.plugins.resource_search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
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

    public static String dailySignIn() {
        HttpResponse httpResponse = HttpRequest.post("https://member.aliyundrive.com/v1/activity/sign_in")
                .header("Content-Type", "application/json")
                .header("Authorization", refreshToken())
                .body("{\"checkDate\": \"2020-12-16\"}")
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.body());
        Boolean success = jsonObject.getBoolean("success");
        if (success == null || !success) {
            return "签到失败, token可能过期";
        }
        SignResult result = jsonObject.getJSONObject("result").toJavaObject(SignResult.class);
        return result.getTitle() + result.getRewardNotice();
    }

    @Getter
    @Setter
    private static class SignResult {
        private Boolean isSignIn;
        private String title;
        private String rewardNotice;
    }
}

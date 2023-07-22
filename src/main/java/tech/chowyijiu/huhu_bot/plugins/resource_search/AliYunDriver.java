package tech.chowyijiu.huhu_bot.plugins.resource_search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.util.Map;

/**
 * @author elastic chow
 * @date 19/7/2023
 */
public class AliYunDriver {

    private static String getAccessToken() {
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
                .header("Authorization", getAccessToken())
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

    public static boolean fileCopy(String shareId) {
        if (shareId == null) return false;
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", getAccessToken(),
                "X-Share-Token", getShareToken(shareId)
        );
        Map<String, Object> body = Map.of(
                "file_id", getShareFileId(shareId),
                "share_id", shareId,
                "auto_rename", true,
                "to_parent_file_id", "root",
                "to_drive_id", "653094272"
        );
        HttpResponse response = HttpRequest.post("https://api.aliyundrive.com/v2/file/copy")
                .addHeaders(headers)
                .body(JSONObject.toJSONString(body))
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        return StringUtil.hasLength(jsonObject.getString("drive_id"));
    }

    public static String getShareToken(String shareId) {
        HttpResponse response = HttpRequest.post("https://api.aliyundrive.com/v2/share_link/get_share_token")
                .header("Content-Type", "application/json")
                .body("{\"share_id\": \"" + shareId + "\"}")
                .execute();
        return JSONObject.parseObject(response.body()).getString("share_token");
    }

    /**
     * 如果有多个文件夹,会有多个,先不考虑
     */
    public static String getShareFileId(String shareId) {
        HttpResponse response = HttpRequest.post("https://api.aliyundrive.com/adrive/v3/share_link/get_share_by_anonymous")
                .header("Content-Type", "application/json")
                .body("{\"share_id\":\"" + shareId + "\"}")
                .execute();
        JSONArray fileInfos = JSONObject.parseObject(response.body()).getJSONArray("file_infos");
        String fileId = "";
        for (Object fileInfo : fileInfos) {
            fileId = ((JSONObject) fileInfo).getString("file_id");
            break;
        }
        return fileId;
    }
}

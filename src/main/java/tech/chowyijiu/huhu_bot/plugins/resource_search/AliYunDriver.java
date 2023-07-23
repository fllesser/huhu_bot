package tech.chowyijiu.huhu_bot.plugins.resource_search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import tech.chowyijiu.huhu_bot.config.BotConfig;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author elastic chow
 * @date 19/7/2023
 */
public class AliYunDriver {

    private static String ACCESS_TOKEN;
    private static LocalDateTime EXPIRE_TIME;
    //2023-07-22T15:35:40Z
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static String getAccessToken() {
        if (EXPIRE_TIME != null && EXPIRE_TIME.isAfter(LocalDateTime.now())) return ACCESS_TOKEN;
        HttpResponse response = HttpRequest.post("https://auth.aliyundrive.com/v2/account/token")
                .header("Content-Type", "application/json")
                .body("{\"grant_type\":\"refresh_token\",\"refresh_token\":\"" + BotConfig.aliRefreshToken + "\"}")
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        ACCESS_TOKEN = jsonObject.getString("access_token");
        //需要+8个小时
        String et = jsonObject.getString("expire_time");
        LocalDateTime utc = LocalDateTime.parse(et, formatter);
        EXPIRE_TIME = utc.plusHours(8L);
        return ACCESS_TOKEN;
    }

    /**
     * 获取签到列表, 并签到
     */
    public static String signInList() {
        String accessToken = getAccessToken();
        if (!StringUtil.hasLength(accessToken)) return "阿里云盘今日签到失败";
        HttpResponse response = HttpRequest.post("https://member.aliyundrive.com/v1/activity/sign_in_list")
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .body("{\"isReward\": \"true\"}")
                .form("_rx-s", "mobile")
                .execute();
        JSONObject result = JSONObject.parseObject(response.body()).getJSONObject("result");
        if (result == null) return "阿里云盘今日签到失败";
        int signInCount = result.getIntValue("signInCount");
        return signInReward(signInCount);
    }

    /**
     * 签到并领取奖励
     */
    private static String signInReward(int signInCount) {
        HttpResponse response = HttpRequest.post("https://member.aliyundrive.com/v1/activity/sign_in_reward")
                .header("Content-Type", "application/json")
                .header("Authorization", ACCESS_TOKEN)
                .body("{\"signInDay\":" + signInCount + "}")
                .form("_rx-s", "mobile")
                .execute();
        JSONObject resp = JSONObject.parseObject(response.body());
        boolean success = resp.getBooleanValue("success");
        if (!success) return "阿里云盘今日签到失败";
        JSONObject result = resp.getJSONObject("result");
        return "阿里云盘今日签到成功, 奖励为:\n"
                + result.getString("name") + ", " + result.getString("description")
                + ", " + result.getString("notice");
    }

    public static boolean fileCopy(String shareId) {
        String accessToken = getAccessToken();
        if (!StringUtil.hasLength(accessToken)) return false;
        String shareToken = getShareToken(shareId);
        if (!StringUtil.hasLength(shareToken)) return false;
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", accessToken,
                "X-Share-Token", shareToken
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

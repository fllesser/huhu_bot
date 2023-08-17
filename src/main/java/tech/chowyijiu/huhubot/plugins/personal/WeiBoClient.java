package tech.chowyijiu.huhubot.plugins.personal;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.config.WeiboConfig;

/**
 * @author flless
 * @date 16/8/2023
 */
@Slf4j
public class WeiBoClient {

    public static boolean check(String pid) {
        HttpResponse response = HttpRequest
                .get("https://weibo.com/p/aj/general/button?api=http://i.huati.weibo.com/aj/super/checkin&id=" + pid)
                .header("cookie", WeiboConfig.cookie)
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        return jsonObject.getIntValue("code") == 100000;
    }
}

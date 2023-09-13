package tech.chowyijiu.huhubot.plugins.personal;

import lombok.extern.slf4j.Slf4j;

/**
 * @author flless
 * @date 16/8/2023
 */
@Slf4j
public class WeiBoClient {

    //public static boolean check(String pid) {
    //    HttpResponse response = HttpRequest
    //            .get("https://weibo.com/p/aj/general/button?api=http://i.huati.weibo.com/aj/super/checkin&id=" + pid)
    //            .header("cookie", WeiboConfig.cookie)
    //            .header("referer", "https://weibo.com/p/" + pid + "/super_index" )
    //            .execute();
    //    JSONObject jsonObject = JSONObject.parseObject(response.body());
    //    System.out.println(jsonObject);
    //    if (jsonObject == null) return false;
    //    int code = jsonObject.getIntValue("code");
    //    return (code == 100000 || code == 382004);
    //}
}

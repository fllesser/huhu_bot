package com.github.huhubot.plugins.resource_search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author elastic chow
 * @date 22/7/2023
 */
//@Component
public class OpenwrtClient {

    public static final String baseUrl = "http://192.168.5.1/cgi-bin/luci";

    public boolean invalidateCache() {
        Boolean ok;
        try (HttpResponse response = HttpRequest.get(baseUrl + "/admin/services/aliyundrive-webdav/invalidate-cache")
                .header("Cookie", login("root", "261806"))
                .execute()) {
            ok = JSONObject.parseObject(response.body()).getBoolean("ok");
        }
        return ok != null && ok;
    }

    public String login(String username, String password) {
        Map<String, Object> map = Map.of("luci_username", username, "luci_password", password);
        try (HttpResponse response = HttpRequest.post(baseUrl)
                .form(map)
                .execute()) {
            return response.header("Set-Cookie");
        }
    }
}

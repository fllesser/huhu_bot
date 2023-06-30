package tech.chowyijiu.huhu_bot.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import lombok.Setter;

/**
 * @author elastic chow
 * @date 29/6/2023
 */
public class SignServerUtil {

    @Setter
    @Getter
    static class Resp {
        private Integer code;
        private String msg;
        private String data;
    }

    public static boolean check() {
        Resp resp = JSON.parseObject(HttpUtil.get("http://localhost:8080"), Resp.class);
        return "success".equals(resp.getMsg());
    }
}

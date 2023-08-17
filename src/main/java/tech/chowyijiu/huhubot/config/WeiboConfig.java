package tech.chowyijiu.huhubot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author flless
 * @date 16/8/2023
 */

@ConfigurationProperties(prefix = "weibo")
public class WeiboConfig {

    public static List<String> pids;
    public static String cookie;

    public void setPid(List<String> pids) {
        WeiboConfig.pids = pids;
    }

    public void setCookie(String cookie) {
        WeiboConfig.cookie = cookie;
    }
}

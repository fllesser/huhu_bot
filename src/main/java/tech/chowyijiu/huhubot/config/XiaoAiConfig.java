package tech.chowyijiu.huhubot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author elastic chow
 * @date 24/7/2023
 */
@Slf4j
@ConfigurationProperties(prefix = "xiaoai")
public class XiaoAiConfig {

    public static String deviceId;
    public static String userId;
    public static String serviceToken;
    public static String securityToken;

    public void setDeviceId(String deviceId) {
        XiaoAiConfig.deviceId = deviceId;
    }

    public void setUserId(String userId) {
        XiaoAiConfig.userId = userId;
    }

    public void setServiceToken(String serviceToken) {
        XiaoAiConfig.serviceToken = serviceToken;
    }

    public void setSecurityToken(String securityToken) {
        XiaoAiConfig.securityToken = securityToken;
    }
}

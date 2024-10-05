package tech.flless.huhubot.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "wx")
public class WxConfig {

    public static String ak;
    public static String sk;

    public void setAk(String ak) {
        WxConfig.ak = ak;
    }

    public void setSk(String sk) {
        WxConfig.sk = sk;
    }
}

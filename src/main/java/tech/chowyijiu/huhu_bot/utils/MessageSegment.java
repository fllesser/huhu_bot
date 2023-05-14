package tech.chowyijiu.huhu_bot.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author elastic chow
 * @date 14/5/2023
 */

public class MessageSegment {

    @Data
    @AllArgsConstructor
    public static class CqCode {
        private String type;
        private final Map<String, String> params = new HashMap<>();

        public void addParam(String key, String value) {
            params.put(key, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[CQ:").append(type);
            params.keySet().forEach(key -> sb.append(",").append(key).append("=").append(params.get(key)));
            sb.append("]");
            return sb.toString();
        }
    }

    public static String image(String url) {
        CqCode cq = new CqCode(CqTypeEnum.image.type);
        cq.addParam("file", url);
        cq.addParam("subType", "0");
        return cq.toString();
    }

    public static String image(File file) {
        return "";
    }

    public static String at(Long userId) {
        CqCode cq = new CqCode("at");
        cq.addParam("qq", userId.toString());
        return cq.toString();
    }
}

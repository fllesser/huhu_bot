package tech.chowyijiu.huhu_bot.entity.gocq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author elastic chow
 * @date 14/5/2023
 */

public class MessageSegmentOld {

    @Data
    @AllArgsConstructor
    public static class CqCode {
        private CqTypeEnum type;
        private final Map<String, String> params = new HashMap<>();

        public void addParam(String key, String value) {
            params.put(key, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[CQ:").append(type.name());
            params.keySet().forEach(key -> sb.append(",").append(key).append("=").append(params.get(key)));
            sb.append("]");
            return sb.toString();
        }

    }

    public static Pattern cqPattern = Pattern.compile("\\[CQ:([a-z]+)((,([a-z]+)=([\\w:/.]+))+)]");

    public static CqCode toCqCode(String cqStr) {
        Matcher matcher = cqPattern.matcher(cqStr);
        if (matcher.find()) {
            String type = matcher.group(1);
            MessageSegmentOld.CqCode cqCode = new MessageSegmentOld.CqCode(CqTypeEnum.valueOf(type));
            String[] split = matcher.group(2).replaceFirst(",", "").split(",");
            for (String s : split) {
                String[] keyValue = s.split("=");
                cqCode.addParam(keyValue[0], keyValue[1]);
            }
            return cqCode;
        }
        return null;
    }

    /**
     * 生成图片CQ码
     * @param url 图片链接
     * @return [CQ:image,file=http://baidu.com/1.jpg]
     */
    public static String image(String url) {
        CqCode cq = new CqCode(CqTypeEnum.image);
        cq.addParam("file", url);
        cq.addParam("subType", "0");
        return cq.toString();
    }

    public static String image(File file) {
        return "";
    }

    /**
     * at某人
     * @param userId 要at的用户qq
     * @return [CQ:at,qq=1943423423]
     */
    public static String at(Long userId) {
        CqCode cq = new CqCode(CqTypeEnum.at);
        cq.addParam("qq", userId.toString());
        return cq.toString();
    }

    /**
     * 戳一戳
     * @param userId 要戳的用户qq
     * @return [CQ:poke,qq=1943423423]
     */
    public static String poke(Long userId) {
        CqCode cq = new CqCode(CqTypeEnum.poke);
        cq.addParam("qq", userId.toString());
        return cq.toString();
    }

    /**
     * 链接分享
     * @param url 链接
     * @param title 标题
     * @param content 内容
     * @param imageUrl 图片url
     * @return [CQ:share,url=http://baidu.com,title=百度]
     */
    public static String share(String url, String title, String content, String imageUrl) {
        CqCode cq = new CqCode(CqTypeEnum.share);
        cq.addParam("url", url);
        cq.addParam("title", title);
        cq.addParam("content", content);
        cq.addParam("image", imageUrl);
        return cq.toString();
    }

    public static String share(String url, String title, String content) {
        CqCode cq = new CqCode(CqTypeEnum.share);
        cq.addParam("url", url);
        cq.addParam("title", title);
        cq.addParam("content", content);
        return cq.toString();
    }

    public static String share(String url, String title) {
        CqCode cq = new CqCode(CqTypeEnum.share);
        cq.addParam("url", url);
        cq.addParam("title", title);
        return cq.toString();
    }

    public static boolean isToMe(Long selfId, String message) {
        CqCode cqCode = MessageSegmentOld.toCqCode(message);
        return cqCode != null && Objects.equals(cqCode.getType(), CqTypeEnum.at)
                && Objects.equals(cqCode.getParams().get("qq"), selfId.toString());
    }
}

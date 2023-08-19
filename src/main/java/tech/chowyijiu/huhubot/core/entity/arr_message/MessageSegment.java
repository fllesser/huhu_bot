package tech.chowyijiu.huhubot.core.entity.arr_message;

import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhubot.core.exception.IllegalDataException;

import java.util.Map;

/**
 * @author elastic chow
 * @date 25/7/2023
 * <p>
 * 不要使用String + MessageSegment发送消息
 * 使用Message.text().append(MessageSegment)
 */
@Getter
@Setter
@SuppressWarnings("unused")
public class MessageSegment {

    private String type;
    private Map<String, Object> data;

    public MessageSegment() {
    }

    private MessageSegment(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }


    public Long getLong(String key) {
        Object o = data.get(key);
        if (o instanceof Long l) {
            return l;
        } else if (o instanceof Integer i) {
            return (long) i;
        } else if (o instanceof String s) {
            return Long.parseLong(s);
        } else {
            throw new IllegalDataException(key + "'s value is not a valid long");
        }
    }

    public String getString(String key) {
        return data.get(key).toString();
    }

    public Integer getInteger(String key) {
        Object o = data.get(key);
        if (o instanceof Integer i) {
            return i;
        } else if (o instanceof String s) {
            return Integer.parseInt(s);
        } else {
            throw new IllegalDataException(key + "'s value is not a valid integer");
        }
    }

    @Override
    @Deprecated
    public String toString() {
        StringBuilder sb = new StringBuilder("[CQ:" + type);
        for (String key : data.keySet()) {
            sb.append(",").append(key).append("=").append(data.get(key));
        }
        sb.append("]");
        return sb.toString();
    }

    public String text() {
        return "text".equals(type) ? getString("text") : "";
    }


    public static MessageSegment build(String type, Map<String, Object> data) {
        return new MessageSegment(type, data);
    }

    public static MessageSegment text(String text) {
        return build("text", Map.of("text", text));
    }

    public static MessageSegment at(Long userId) {
        return build("at", Map.of("qq", userId));
    }

    public static MessageSegment atAll() {
        return build("at", Map.of("qq", "all"));
    }

    public static MessageSegment tts(String text) {
        return build("tts", Map.of("text", text));
    }

    public static MessageSegment image(String url, int cache, int threadNum) {
        if (!url.startsWith("http"))
            throw new IllegalArgumentException("url string must start with 'http'");
        return build("image", Map.of("file", url, "cache", cache, "c", threadNum));
    }

    public static MessageSegment image(String url, int cache) {
        return image(url, cache, 1);
    }

    public static MessageSegment image(String file) {
        if (!file.startsWith("http") && !file.startsWith("file://") && !file.startsWith("base64://"))
            throw new IllegalArgumentException("file string must start with 'http' or 'file://' or 'base64://'");
        return build("image", Map.of("file", file));
    }

    public static MessageSegment poke(Long userId) {
        return build("poke", Map.of("qq", userId));
    }

    /**
     * 回复指定消息
     * [CQ:reply,id=123456]
     * 范围: 发送/接收
     * https://docs.go-cqhttp.org/cqcode/#%E5%9B%9E%E5%A4%8D
     *
     * @param messageId 回复时所引用的消息id, 必须为本群消息.
     * @return MessageSegment
     */
    public static MessageSegment reply(Long messageId) {
        return build("reply", Map.of("id", messageId));
    }

    /**
     * 回复自定义消息
     * 范围: 发送/接收
     * https://docs.go-cqhttp.org/cqcode/#%E5%9B%9E%E5%A4%8D
     *
     * @param text      自定义回复的信息
     * @param qq        自定义回复时的自定义QQ, 如果使用自定义信息必须指定.
     * @param timestamp 自定义回复时的时间, 格式为Unix时间
     * @param seq       起始消息序号, 可通过 get_msg 获得
     * @return MessageSegment
     */
    public static MessageSegment reply(String text, Long qq, Long timestamp, Long seq) {
        return build("reply", Map.of("text", text, "qq", qq, "time", timestamp, "seq", seq));
    }


    /**
     * 链接分享
     * https://docs.go-cqhttp.org/cqcode/#%E9%93%BE%E6%8E%A5%E5%88%86%E4%BA%AB
     *
     * @param url      要分享的URL
     * @param title    标题
     * @param content  发送时可选, 内容描述
     * @param imageUrl 发送时可选, 图片 URL
     * @return MessageSegment
     */
    public static MessageSegment share(String url, String title, String content, String imageUrl) {
        return build("share", Map.of("url", url, "title", title, "content", content, "image", imageUrl));
    }
}

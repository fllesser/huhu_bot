package tech.chowyijiu.huhubot.entity.arr_message;

import lombok.Getter;
import lombok.Setter;
import tech.chowyijiu.huhubot.exception.IllegalDataException;

import java.util.Map;

/**
 * @author elastic chow
 * @date 25/7/2023
 */
@Getter
@Setter
public class MessageSegment {

    private String type;
    private Map<String, Object> data;

    public MessageSegment() {
    }

    public MessageSegment(String type, Map<String, Object> data) {
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
        Object o = data.get(key);
        if (o instanceof String s) {
            return s;
        } else {
            return o.toString();
        }
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

    public String text() {
        return "text".equals(type) ? getString("text") : "";
    }


    public static MessageSegment init(String type, Map<String, Object> data) {
        return new MessageSegment(type, data);
    }

    public static MessageSegment text(String text) {
        return init("text", Map.of("text", text));
    }

    public static MessageSegment at(Long userId) {
        return init("at", Map.of("qq", userId));
    }

    public static MessageSegment atAll() {
        return init("at", Map.of("qq", "all"));
    }

    public static MessageSegment tts(String text) {
        return init("tts", Map.of("text", text));
    }

    public static MessageSegment image(String url, int cache, int threadNum) {
        if (!url.startsWith("http"))
            throw new IllegalArgumentException("url string must start with 'http'");
        return init("image", Map.of("file", url, "cache", cache, "c", threadNum));
    }

    public static MessageSegment image(String url, int cache) {
        return image(url, cache, 1);
    }

    public static MessageSegment image(String file) {
        if (!file.startsWith("http") && !file.startsWith("file://") && !file.startsWith("base64://"))
            throw new IllegalArgumentException("file string must start with 'http' or 'file://' or 'base64://'");
        return init("image", Map.of("file", file));
    }

    public static MessageSegment poke(Long userId) {
        return init("poke", Map.of("qq", userId));
    }
}

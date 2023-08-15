package tech.chowyijiu.huhubot.core.entity.str_message;

import lombok.RequiredArgsConstructor;
import tech.chowyijiu.huhubot.utils.StringUtil;

import java.util.HashMap;

/**
 * @author elastic chow
 * @date 3/7/2023
 */
@Deprecated
@RequiredArgsConstructor
public class MessageSegment extends HashMap<String, String> {

    //text, at...
    private final String type;

    public String getType() {
        return type;
    }

    public String getText() {
        return "text".equals(type) ? get("text") : "";
    }

    public long getLong(String key) {
        String val = this.get(key);
        if (StringUtil.isDigit(val)) return Long.parseLong(val);
        else return -1;
    }

    @Override
    public String toString() {
        if ("text".equals(type)) return get("text");
        StringBuilder sb = new StringBuilder();
        sb.append("[CQ:").append(this.type);
        this.keySet().forEach(key -> sb.append(",").append(key).append("=").append(this.get(key)));
        sb.append("]");
        return sb.toString();
    }

    public static MessageSegment build(String type, String... params) {
        MessageSegment segment = new MessageSegment(type);
        int length = params.length;
        if (length > 0) {
            if (length % 2 != 0) return null;
            for (int i = 0; i < params.length; i += 2) {
                //if (StringUtil.hasLength(params[i + 1]))
                //这里就不判断了, 因为如果为"", get出来反而为null
                segment.put(params[i], params[i + 1]);
            }
        } else segment.put("error", "No valid parameters");
        return segment;
    }

    public static MessageSegment text(String text) {
        return build("text", "text", text);
    }

    public static MessageSegment at(Long userId) {
        return build("at", "qq", String.valueOf(userId));
    }

    public static MessageSegment atAll() {
        return build("at", "qq", "all");
    }


    public static MessageSegment tts(String text) {
        return build("tts", "text", text);
    }

    public static MessageSegment image(String file, Integer cache, Integer threadNum) {
        if (!file.startsWith("http") && !file.startsWith("file://") && !file.startsWith("base64://"))
            return build("image", "error", "Incorrect format of 'file' parameter");
        return build("image", "file", file, "cache", String.valueOf(cache),
                "c", String.valueOf(threadNum));
    }

    public static MessageSegment image(String file, Integer cache) {
        if (!file.startsWith("http")) cache = null;
        return image(file, cache, null);
    }

    public static MessageSegment image(String file) {
        return image(file, null, null);
    }

    public static MessageSegment poke(Long userId) {
        return build("poke", "qq", String.valueOf(userId));
    }

}

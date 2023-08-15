package tech.chowyijiu.huhubot.core.entity.str_message;

import tech.chowyijiu.huhubot.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author elastic chow
 * @date 3/7/2023
 */
@Deprecated
public class Message extends ArrayList<MessageSegment> {

    /**
     * @param msg raw_message
     * @return Message
     */
    public static Message build(String msg) {
        Message message = new Message();
        if (!StringUtil.hasLength(msg)) return message;
        if (!msg.contains("[CQ:")) {
            message.add(MessageSegment.text(msg));
            return message;
        }
        int fromIndex = 0, start = 0, end = 0;
        //获取第一个索引
        while (true) {
            start = msg.indexOf("[CQ:", fromIndex);
            //如果没有cq了, 把剩余的部分添加成text
            if (start == -1) {
                if (msg.length() > fromIndex) message.add(MessageSegment.text(msg.substring(fromIndex)));
                break;
            }
            //如果cq的start前面还有字符串, 先把这一部分添加成text
            if (start - fromIndex > 0) message.add(MessageSegment.text(msg.substring(fromIndex, start)));
            end = msg.indexOf("]", start);
            //添加cq: type,xxx=xxx,xxx=xxx
            String cq = msg.substring(start + 4, end);
            String[] split = cq.split(",");
            MessageSegment segment = new MessageSegment(split[0]);
            for (int i = 1; i < split.length; i++) {
                String[] keyVal = split[i].split("=");
                segment.put(keyVal[0], keyVal[1]);
            }
            message.add(segment);
            fromIndex = end + 1;
        }
        return message;
    }

    public void add(String text) {
        this.add(MessageSegment.text(text));
    }

    public boolean checkToMe(Long selfId) {
        for (MessageSegment segment : this) {
            if ("at".equals(segment.getType()) ) {
                String qq = segment.get("qq");
                if ("all".equals(qq)) return true;
                else return Long.parseLong(qq) == selfId;
            }
        }
        return false;
    }

    /**
     * 获取所有text信息, 去除所有cq码
     * @return String
     */
    public String plainText() {
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : this) {
            //防止加个null进去
            //StringUtil.hasLength(segment.getText(), sb::append);
            sb.append(segment.getText());
        }
        return sb.toString();
    }

    /**
     * 用于拼接要发送的字符串, 例如: 早上好[CQ:at,qq123]
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : this) sb.append(segment);
        return sb.toString();
    }

    public List<MessageSegment> getSegmentByType(String type) {
        List<MessageSegment> segments = new ArrayList<>();
        for (MessageSegment segment : this) {
            if (Objects.equals(type, segment.getType())) segments.add(segment);
        }
        return segments;
    }

    public String toArrayString() {
        return Arrays.toString(this.toArray());
    }
}

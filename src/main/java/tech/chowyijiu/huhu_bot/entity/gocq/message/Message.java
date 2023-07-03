package tech.chowyijiu.huhu_bot.entity.gocq.message;

import tech.chowyijiu.huhu_bot.constant.CqTypeEnum;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author elastic chow
 * @date 3/7/2023
 */
public class Message extends ArrayList<MessageSegment> {

    /**
     * 字符串转Message对象, 用于转换事件的消息
     *
     * @param str message
     * @return Message
     */
    public static Message build(String str) {
        Message message = new Message();
        //message.strMessage = str;
        if (!str.contains("[CQ:")) {
            message.add(MessageSegment.text(str));
            return message;
        }
        int fromIndex = 0, start = 0, end = 0;
        //获取第一个索引
        while (true) {
            start = str.indexOf("[CQ:", fromIndex);
            //如果没有cq了, 直接把剩余的部分添加成text
            if (start == -1) {
                if (str.length() > fromIndex) {
                    message.add(MessageSegment.text(str.substring(fromIndex)));
                }
                break;
            }
            //如果cq的start前面还有字符串, 先把这一部分添加成text
            if (start - fromIndex > 0) {
                message.add(MessageSegment.text(str.substring(fromIndex, start)));
            }
            end = str.indexOf("]", start);
            //添加cq
            String cq = str.substring(start + 4, end);
            String[] split = cq.split(",");
            MessageSegment segment = new MessageSegment(CqTypeEnum.valueOf(split[0]).name());
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

    public boolean isToMe(Long selfId) {
        for (MessageSegment segment : this) {
            if ("at".equals(segment.getType()) &&
                    Objects.equals(Long.parseLong(segment.get("qq")), selfId)
            ) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : this) {
            String text = segment.getText();
            if (StringUtil.hasLength(text)) sb.append(text);
            else sb.append(segment);
        }
        return sb.toString();
    }

    public List<MessageSegment> getSegmentByType(String type) {
        List<MessageSegment> segments = new ArrayList<>();
        for (MessageSegment segment : this) {
            if (segment.getType().equals(type)) segments.add(segment);
        }
        return segments;
    }

    public String toJsonArray() {
        return Arrays.toString(this.toArray());
    }
}

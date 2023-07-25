package tech.chowyijiu.huhu_bot.entity.arr_message;

import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 25/7/2023
 */
public class Message extends ArrayList<MessageSegment> {

    private String plainText;

    public boolean checkToMe(Long selfId) {
        for (MessageSegment segment : this) {
            if ("at".equals(segment.getType()) ) {
                String qq = segment.getString("qq");
                if ("all".equals(qq)) return true;
                else return Long.parseLong(qq) == selfId;
            }
        }
        return false;
    }

    public Message add(String text) {
        this.add(MessageSegment.text(text));
        return this;
    }

    public static Message text(String text) {
        Message message = new Message();
        message.add(MessageSegment.text(text));
        return message;
    }

    /**
     * 获取所有text信息, 去除所有cq码
     * @return String
     */
    public String plainText() {
        if (this.plainText != null) return this.plainText;
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : this) sb.append(segment.text());
        this.plainText = sb.toString();
        return this.plainText;
    }

    public List<MessageSegment> getSegmentByType(String type) {
        return this.stream().filter(seg -> seg.getType().equals(type)).toList();
    }

    @Override
    public String toString() {
        //return this.plainText;
        return JSONObject.toJSONString(this);
    }
}

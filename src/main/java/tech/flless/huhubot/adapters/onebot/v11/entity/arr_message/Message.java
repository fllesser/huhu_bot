package tech.flless.huhubot.adapters.onebot.v11.entity.arr_message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elastic chow
 * @date 25/7/2023
 */
@Getter
public class Message extends ArrayList<MessageSegment> {

    @JsonIgnore
    private String plainText;

    public static Message reply(Long messageId) {
        Message message = new Message();
        return message.append(MessageSegment.reply(messageId));
    }

    public static Message text(String text) {
        return new Message().append(text);
    }

    public boolean checkToMe(Long selfId) {
        for (MessageSegment segment : this) {
            if ("at".equals(segment.getType())) {
                String qq = segment.getString("qq");
                //qq只有all或者纯数字的情况
                if (!"all".equals(qq) && Long.parseLong(qq) == selfId) return true;
            }
        }
        return false;
    }


    /**
     * 追加文本消息
     *
     * @param text String
     * @return this
     */
    public Message append(String text) {
        this.add(MessageSegment.text(text));
        return this;
    }

    /**
     * 追加非文本消息
     *
     * @param segment MessageSegment
     * @return this
     */
    public Message append(MessageSegment segment) {
        this.add(segment);
        return this;
    }



    /**
     * 初始化所有text信息, 去除所有cq码
     * 即合并所有[CQ:text,text=?]中的?
     */
    public void plainText() {
        StringBuilder sb = new StringBuilder();
        for (MessageSegment segment : this) sb.append(segment.text());
        this.plainText = sb.toString().trim();
    }

    /**
     * 根据MessageSegment的type获取指定类型的所有消息段
     * 比如 测试[CQ:at,qq=123123]测试[CQ:at,qq=123123]测试
     * 使用get("at")可以得到[MessageSegment([CQ:at,qq=123123]), MessageSegment([CQ:at,qq=123123])]
     *
     * @param type String
     * @return List<MessageSegment>
     */
    public List<MessageSegment> get(String type) {
        return this.stream().filter(seg -> seg.getType().equals(type)).toList();
    }

    public MessageSegment get(String type, int index) {
        List<MessageSegment> segments = this.get(type);
        if (segments.size() > index) return segments.get(index);
        return null;
    }

}

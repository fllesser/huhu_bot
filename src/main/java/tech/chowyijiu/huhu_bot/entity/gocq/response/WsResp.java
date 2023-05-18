package tech.chowyijiu.huhu_bot.entity.gocq.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Data
@ToString
public class WsResp implements Serializable {

    @JSONField(name = "post_type")
    private String postType;
    @JSONField(name = "meta_event_type")
    private String metaEventType;
    @JSONField(name = "message_type")
    private String messageType;
    @JSONField(name = "notice_type")
    private String noticeType;
    // 操作人id 比如群管理员a踢了一个人,那么该值为a的qq号
    @JSONField(name = "operator_id")
    private Long operatorId;
    private Long time;
    @JSONField(name = "self_id")
    private Long selfId;
    @JSONField(name = "sub_type")
    private String subType;
    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "sender_id")
    private Long senderId;
    @JSONField(name = "group_id")
    private Long groupId;
    @JSONField(name = "target_id")
    private String targetId;
    private String message;
    @JSONField(name = "raw_message")
    private String rawMessage;
    private Integer font;
    private Sender sender;
    @JSONField(name = "message_id")
    private String messageId;
    @JSONField(name = "message_seq")
    private Integer messageSeq;
    private String anonymous;
    private Long duration; //禁言时长
    @JSONField(name = "card_new")
    private String cardNew;
    @JSONField(name = "cardOld")
    private String cardOld;
}

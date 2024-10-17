package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 群聊撤回事件
 *
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
public class GroupRecallNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.group_recall.name();

    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "group_id")
    private Long groupId;
    @JSONField(name = "operator_id")
    private Long operatorId;
    @JSONField(name = "message_id")
    private Long messageId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "[", "]")
                .add("messageId=" + messageId)
                .add("userId=" + userId)
                .add("operatorId=" + operatorId)
                .add("groupId=" + groupId)
                .toString();
    }
}

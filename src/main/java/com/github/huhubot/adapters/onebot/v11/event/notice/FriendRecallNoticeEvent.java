package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 好友撤回事件
 *
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
public class FriendRecallNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.friend_recall.name();
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("message_id")
    private Long messageId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "[", "]")
                .add("messageId=" + messageId)
                .add("userId=" + userId)
                .toString();
    }
}

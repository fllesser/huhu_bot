package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * @author elastic chow
 * @date 18/5/2023
 */

@Getter
@Setter
public class NotifyNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.notify.name();
    @JsonProperty("sub_type")
    private String subType;
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("sender_id")
    private Long senderId;
    @JsonProperty("target_id")
    private Long targetId;
    private String title;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "." + subType + "[", "]")
                .add("userId=" + userId)
                .add("groupId=" + groupId)
                .add("senderId=" + senderId)
                .add("targetId=" + targetId)
                .add("title='" + title + "'")
                .toString();
    }
}

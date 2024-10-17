package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 好友添加事件
 */
@Getter
@Setter
public class FriendAddNoticeEvent extends NoticeEvent {

    private final String noticeType = NoticeTypeEnum.friend_add.name();

    @JsonProperty("user_id")
    private Long userId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "[", "]")
                .add("userId=" + userId)
                .toString();
    }
}

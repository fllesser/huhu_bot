package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 群管理员变动事件
 *
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
public class GroupAdminNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_admin.name();
    @JsonProperty("sub_type")
    private String subType; //approve、invite 事件子类型, 分别表示管理员已同意入群、管理员邀请入群
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("group_id")
    private Long groupId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "." + subType + "[", "]")
                .add("userId=" + userId)
                .add("groupId=" + groupId)
                .toString();
    }
}

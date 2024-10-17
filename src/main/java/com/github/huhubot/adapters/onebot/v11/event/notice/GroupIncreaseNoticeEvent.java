package com.github.huhubot.adapters.onebot.v11.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.NoticeTypeEnum;

import java.util.StringJoiner;

/**
 * 群成员增加事件
 *
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
public class GroupIncreaseNoticeEvent extends NoticeEvent {
    private final String noticeType = NoticeTypeEnum.group_increase.name();

    //事件子类型
    //approve分别表示管理员已同意入群  invite 管理员邀请入群
    @JsonProperty("sub_type")
    private String subType;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("operator_id")
    private Long operatorId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "notice." + noticeType + "." + subType + "[", "]")
                .add("operatorId=" + operatorId)
                .add("userId=" + userId)
                .add("groupId=" + groupId)
                .toString();
    }
}
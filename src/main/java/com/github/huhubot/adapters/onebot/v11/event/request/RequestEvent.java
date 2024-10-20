package com.github.huhubot.adapters.onebot.v11.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;
import com.github.huhubot.adapters.onebot.v11.event.Event;

import java.util.StringJoiner;

@Getter
@Setter
public class RequestEvent extends Event {
    private final String postType = PostTypeEnum.request.name();

    @JsonProperty("request_type")
    private String requestType; //friend group 加好友 入群请求

    @JsonProperty("user_id")
    private Long userId;    //发送请求的 QQ 号
    private String comment; //验证信息
    private String flag;    //请求 flag, 在调用处理请求的 API 时需要传入

    //request_type 为group时 特有
    @JsonProperty("sub_type")
    private String subType; //append、invite	请求子类型, 分别表示加群请求、邀请登录号入群
    @JsonProperty("group_id")
    private Long groupId;

    @Override
    public String toString() {
        return new StringJoiner(", ", "request." + requestType + "[", "]")
                .add("userId=" + userId)
                .add("comment='" + comment + "'")
                .add("flag='" + flag + "'")
                .add("subType='" + subType + "'")
                .add("groupId=" + groupId)
                .toString();
    }
}

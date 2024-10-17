package com.github.huhubot.adapters.onebot.v11.entity.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 发送者信息
 * @author elastic chow
 * @date 13/5/2023
 */
@Data
public class Sender {
    private int age;
    private String area;
    private String card;
    private String level;
    private String role;
    private String nickname;
    private String sex;
    private String title;

    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "group_id")
    private Long groupId;

}
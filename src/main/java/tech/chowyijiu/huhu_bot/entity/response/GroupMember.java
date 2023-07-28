package tech.chowyijiu.huhu_bot.entity.response;

/**
 * 群成员信息
 *
 * @author elastic chow
 * @date 13/5/2023
 */

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 群成员信息
 */
@Data
public class GroupMember {
    private int age;
    private String area;
    private String card;
    @JSONField(name = "card_changeable")
    private boolean cardChangeable;
    @JSONField(name = "group_id")
    private Long groupId;
    @JSONField(name = "join_time")
    private long joinTime;
    @JSONField(name = "last_sent_time")
    private long lastSentTime;
    private String level;
    private String nickname;
    private String role;
    private String sex;
    @JSONField(name = "shut_up_timestamp")
    private int shutUpTimestamp;
    private String title;
    @JSONField(name = "title_expire_time")
    private int titleExpireTime;
    private boolean unfriendly;
    @JSONField(name = "user_id")
    private Long userId;
}
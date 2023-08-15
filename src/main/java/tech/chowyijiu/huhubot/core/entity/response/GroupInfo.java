package tech.chowyijiu.huhubot.core.entity.response;

/**
 * 群信息
 * @author elastic chow
 * @date 13/5/2023
 */

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class GroupInfo {
    @JSONField(name = "group_id")
    private Long groupId;
    @JSONField(name = "group_name")
    private String groupName;
    @JSONField(name = "group_memo")
    private String groupMemo;
    @JSONField(name = "group_create_time")
    private Long groupCreateTime;
    @JSONField(name = "group_level")
    private Integer groupLevel;
    @JSONField(name = "member_count")
    private Integer memberCount;
    @JSONField(name = "max_member_count")
    private Integer maxMemberCount;
}
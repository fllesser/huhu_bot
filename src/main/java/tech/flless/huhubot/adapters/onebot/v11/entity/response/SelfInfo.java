package tech.flless.huhubot.adapters.onebot.v11.entity.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
public class SelfInfo {
    @JSONField(name = "user_id")
    private Long userId;
    private String nickname;
}
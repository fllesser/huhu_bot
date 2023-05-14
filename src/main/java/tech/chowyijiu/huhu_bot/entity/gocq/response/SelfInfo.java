package tech.chowyijiu.huhu_bot.entity.gocq.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
public class SelfInfo implements Serializable {
    @JSONField(name = "user_id")
    private Long userId;
    private String nickname;
}
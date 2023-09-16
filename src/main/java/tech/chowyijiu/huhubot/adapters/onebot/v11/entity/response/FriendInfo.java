package tech.chowyijiu.huhubot.adapters.onebot.v11.entity.response;

import lombok.Data;

/**
 * @author elastic chow
 * @date 23/5/2023
 */
@Data
public class FriendInfo {

    private String userId;
    private String nickname;
    private String remark; //备注
}

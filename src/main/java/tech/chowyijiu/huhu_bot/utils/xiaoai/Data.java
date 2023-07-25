package tech.chowyijiu.huhu_bot.utils.xiaoai;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * @author elastic chow
 * @date 24/7/2023
 */
@Getter
@Setter
public class Data {

    private ActionParams params;

    public Data(ActionParams params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

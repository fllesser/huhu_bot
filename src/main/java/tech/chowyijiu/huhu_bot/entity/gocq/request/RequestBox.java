package tech.chowyijiu.huhu_bot.entity.gocq.request;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Data
@Accessors(chain = true)
public class RequestBox<T> {

    private String action;
    private T params;
    private String echo; //"'回声', 如果请求时指定了 echo, 那么响应也会包含 echo"

}

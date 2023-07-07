package tech.chowyijiu.huhu_bot.entity.gocq.request;

import lombok.Data;

import java.util.Map;


/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Data
public class RequestBox {

    private String action;
    private Map<String, Object> params;
    private String echo; //回声, 如果请求时指定了 echo, 那么响应也会包含 echo

}

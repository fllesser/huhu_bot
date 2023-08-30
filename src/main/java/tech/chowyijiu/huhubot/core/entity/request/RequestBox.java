package tech.chowyijiu.huhubot.core.entity.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;


/**
 * @author elastic chow
 * @date 13/5/2023
 */
@Builder
@Getter
public class RequestBox {

    private String action;
    private Map<String, Object> params;
    private long echo; //回声, 如果请求时指定了 echo, 那么响应也会包含 echo

}

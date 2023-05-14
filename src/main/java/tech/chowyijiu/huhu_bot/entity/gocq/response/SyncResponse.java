package tech.chowyijiu.huhu_bot.entity.gocq.response;

import lombok.Data;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
public class SyncResponse<T> {

    public static transient final String STATUS_OK = "ok";

    private Integer retcode;
    private String status;
    private String echo;
    private String wording;
    private String msg;
    private T data;
}
package tech.chowyijiu.huhu_bot.event.echo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.event.Event;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString(exclude = {"data"})
public class EchoEvent extends Event {
    private String data;
    private String echo; //回声
    private String message;
    private String retcode;

    //status
    //ok	    api 调用成功
    //async     api 调用已经提交异步处理, 此时 retcode 为 1, 具体 api 调用是否成功无法得知
    //failed	api 调用失败
    private String status;
}

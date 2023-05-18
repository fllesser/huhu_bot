package tech.chowyijiu.huhu_bot.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString(exclude = {"data"})
public class EchoEvent extends Event{
    private String data;
    private String echo;
    private String message;
    private String retcode;
    private String status;
}

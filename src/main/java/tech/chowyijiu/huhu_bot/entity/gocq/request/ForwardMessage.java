package tech.chowyijiu.huhu_bot.entity.gocq.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
@NoArgsConstructor
public class ForwardMessage {
    private final String type = "node";
    private Data_ data;

    public ForwardMessage(Data_ data) {
        this.data = data;
    }

    public ForwardMessage(String name, Long uin, String content) {
        this.data = new Data_(name, uin, content);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data_ {
        private String name;
        private Long uin;
        private String content;
    }
}

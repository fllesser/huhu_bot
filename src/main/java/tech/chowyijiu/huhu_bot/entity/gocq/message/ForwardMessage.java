package tech.chowyijiu.huhu_bot.entity.gocq.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ForwardMessage node 这是自定义转发消息的一个node, 发送即为ForwardMessage[]
 * @author elastic chow
 * @date 14/5/2023
 */
@Data
@NoArgsConstructor
public class ForwardMessage {
    private final String type = "node";

    @JSONField(name = "data")
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
        private String name;    //发送者显示名字	用于自定义消息 (自定义消息并合并转发, 实际查看顺序为自定义消息段顺序)
        private Long uin;       //uin	int64	发送者QQ号	用于自定义消息
        private String content; //用MessageSegment组合构建
    }
}

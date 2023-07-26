package tech.chowyijiu.huhu_bot.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.Event;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MetaEvent extends Event {

    private final String postType = PostTypeEnum.meta_event.name();
    @JsonProperty("meta_event_type")
    private String metaEventType;
    @JsonProperty("sub_type")
    private String subType;

}

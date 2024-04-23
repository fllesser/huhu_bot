package tech.flless.huhubot.adapters.onebot.v11.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.flless.huhubot.adapters.onebot.v11.event.Event;
import tech.flless.huhubot.core.constant.PostTypeEnum;

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


    public boolean connect() {
        return "lifecycle".equals(this.getMetaEventType()) && "connect".equals(this.getSubType());
    }

    public boolean heartbeat() {
        return "heartbeat".equals(this.getMetaEventType());
    }

}

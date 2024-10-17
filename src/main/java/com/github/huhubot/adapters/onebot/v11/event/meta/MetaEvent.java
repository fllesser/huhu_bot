package com.github.huhubot.adapters.onebot.v11.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;

import java.util.StringJoiner;

/**
 * @author elastic chow
 * @date 16/5/2023
 */
@Getter
@Setter
@NoArgsConstructor
public class MetaEvent extends Event {

    private final String postType = PostTypeEnum.meta_event.name();
    @JsonProperty("meta_event_type")
    private String metaEventType;
    @JsonProperty("sub_type")
    private String subType;


    public boolean isConnected() {
        return "lifecycle".equals(this.getMetaEventType()) && "connect".equals(this.getSubType());
    }

    public boolean isHeartbeat() {
        return "heartbeat".equals(this.getMetaEventType());
    }

    @Override
    public String toString() {
        return "meta." + metaEventType + (subType == null ? "" : "." + subType);
    }
}

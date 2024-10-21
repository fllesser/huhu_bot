package com.github.huhubot.adapters.onebot.v11.event;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.huhubot.adapters.onebot.v11.bot.BotContainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.github.huhubot.adapters.onebot.v11.bot.Bot;
import com.github.huhubot.adapters.onebot.v11.event.message.MessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.meta.MetaEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import com.github.huhubot.adapters.onebot.v11.event.request.RequestEvent;
import com.github.huhubot.adapters.onebot.v11.constant.PostTypeEnum;
import com.github.huhubot.utils.StringUtil;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@Setter
public abstract class Event {

    @JsonIgnore
    private Bot bot;

    @JsonProperty("self_id")
    private Long selfId;
    @JsonProperty("post_type")
    private String postType;
    private Long time;

    public void init() {
        this.setBot(BotContainer.getBot(this.getSelfId()));
        log.info("[hb]<-ws-[ob-{}] {}", this.getSelfId(), this);
    }
}

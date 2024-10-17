package com.github.huhubot.plugins.ai.reecho.entity.resp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AsyncGenResp {
    private Data data;

    @Setter
    @Getter
    public static class Data {
        private String id;
    }
}
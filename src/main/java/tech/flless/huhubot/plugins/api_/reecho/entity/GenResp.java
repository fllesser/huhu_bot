package tech.flless.huhubot.plugins.api_.reecho.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class GenResp {
    private Data data;

    @Setter
    @Getter
    public static class Data {
        private String id;
    }
}
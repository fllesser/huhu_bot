package com.github.huhubot.plugins.ai.reecho.entity.resp;


import lombok.Data;

import java.util.List;

@Data
public class RoleList {

    private int status; //200
    //private String message; //ok
    private List<Role> data;

    @Data
    public static class Role {
        private String id;
        private String name;
        private MetaData metadata;
    }

    @Data
    public static class MetaData {
        private String avatar;
        private String description;
    }

}
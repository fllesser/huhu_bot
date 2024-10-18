package com.github.huhubot.plugins.ai.reecho.entity.resp;

import lombok.Data;

@Data
public class AccountInfo {


    private Integer status;
    private String message;
    private User user;

    @Data
    public static class User {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String avatar;
        private String role;
        private String isPaid;
        private Integer notifications;
        private Long credits;
    }



}

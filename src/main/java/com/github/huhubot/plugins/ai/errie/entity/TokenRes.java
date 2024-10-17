package com.github.huhubot.plugins.ai.errie.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TokenRes {

    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expiresIn")
    private long expiresIn;
    @JsonProperty("sessionKey")
    private String sessionKey;
    @JsonProperty("access_token")
    private String accessToken;
    private String scope;
    @JsonProperty("sessionSecret")
    private String sessionSecret;

}
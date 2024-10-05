package tech.flless.huhubot.plugins.ai.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TokenRes {

    private String refresh_token;
    private long expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;

}
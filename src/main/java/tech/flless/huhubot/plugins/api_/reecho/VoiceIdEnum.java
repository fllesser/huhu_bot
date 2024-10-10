package tech.flless.huhubot.plugins.api_.reecho;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoiceIdEnum {

    LeiJun("market:6e2463c6-ccff-4d12-b6bf-d5ab9ec65573"),
    LaoDie("market:91be745a-6626-4045-9046-297f706d1855"),
    LuBenWei("market:1032c707-fda4-4d88-9c8c-db17010e7141"),
    MaiKeASe("market:aff33d21-c6a9-49a1-a575-05473c0c5a85"),
    DingZhen("market:aff33d21-c6a9-49a1-a575-05473c0c5a85"),
    GuoDeGang("market:3e11f0db-5b65-4daa-959a-4b44f5eb0d3f")
    ;
    private final String voiceId;
}

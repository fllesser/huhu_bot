package tech.chowyijiu.huhu_bot.constant;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
public enum CqTypeEnum {
    text,

    image,
    reply,
    poke,
    share,
    face,
    record,
    video,
    at,
    music,
    redbag,
    tts,            //文本转语音 发 通过TX的TTS接口, 采用的音源与登录账号的性别有关 范围: 仅群聊

    //gocq 未支持
    rps,
    dice,
    shake,
    contact,
    location,
    anonymous;

}

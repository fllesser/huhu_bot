package tech.chowyijiu.huhubot.constant;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
public enum SubTypeEnum {
    // 戳一戳
    poke,
    // 红包运气王
    lucky_king,
    // 荣誉变更 龙王等
    honor,
    // 头衔变更
    title,
    // 被批准进入(管理直接邀请也是这个)
    approve,
    // 自行离开
    leave,
    // 被踢出
    kick,

    connect // MetaEvent
}

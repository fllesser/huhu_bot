package tech.flless.huhubot.core.rule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 20/6/2023
 */
@Getter
@RequiredArgsConstructor
public enum RuleEnum {

    tome("@机器人", RuleRef::tome),
    to_all("@全体", RuleRef::toAll),
    superuser("使用者为超级用户", RuleRef::superuser),
    owner("使用者为群主", RuleRef::owner),
    admin("使用者为 管理 or 群主 or 超级用户", RuleRef::admin),
    self_owner("本体为群主", RuleRef::selfOwner),
    self_admin("本体为管理 or 群主", RuleRef::selfAdmin),
    temp_session("群临时会话", RuleRef::tempSession);

    private final String description;
    private final Rule rule;
}

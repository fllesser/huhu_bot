package tech.chowyijiu.huhu_bot.core.rule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elastic chow
 * @date 20/6/2023
 */
@Getter
@RequiredArgsConstructor
public enum RuleEnum {

    default_("默认", (bot, event) -> true),
    tome("@机器人", RuleImpl::tome),
    superuser("使用者为超级用户", RuleImpl::superuser),
    owner("使用者为群主", RuleImpl::owner),
    admin("使用者为 管理 or 群主 or 超级用户", RuleImpl::admin),
    self_owner("机器人号为群主", RuleImpl::selfOwner),
    self_admin("机器人号为管理 or 群主", RuleImpl::selfAdmin);

    private final String description;
    private final Rule rule;
}

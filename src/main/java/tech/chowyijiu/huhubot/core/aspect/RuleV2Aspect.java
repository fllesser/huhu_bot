package tech.chowyijiu.huhubot.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import tech.chowyijiu.huhubot.core.annotation.RuleV2;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.ws.Bot;

import java.lang.reflect.Method;

/**
 * @author elastic chow
 * @date 2/7/2023
 */

@Slf4j
//@Component
//@Aspect
@Deprecated
public class RuleV2Aspect {

    @Pointcut("@annotation(tech.chowyijiu.huhubot.core.annotation.RuleV2)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && args(bot,event)", argNames = "joinPoint,bot,event")
    public Object doAround(ProceedingJoinPoint joinPoint, Bot bot, Event event) {
        log.info("RuleV2Aspect.doBefore");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RuleV2 annotation = method.getAnnotation(RuleV2.class);

        log.info("Bot:{}, Event:{}", bot, event);
        Object result = null;
        if (annotation.rule().getRule().check(bot, event)) {
            try {
                result = joinPoint.proceed();
            } catch (Throwable e) {
                log.error("异常通知", e);
            }
        } else {
            log.info("规则不匹配, 忽略");
        }
        return result;
    }


}

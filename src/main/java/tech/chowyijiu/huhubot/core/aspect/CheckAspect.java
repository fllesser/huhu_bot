package tech.chowyijiu.huhubot.core.aspect;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhubot.core.annotation.CoolDown;
import tech.chowyijiu.huhubot.core.annotation.RuleCheck;
import tech.chowyijiu.huhubot.core.event.Event;
import tech.chowyijiu.huhubot.core.utils.TimeLimiter;

import java.lang.reflect.Method;

/**
 * @author elastic chow
 * @date 2/7/2023
 */

@Slf4j
@Component
@Aspect
public class CheckAspect {

    @Pointcut("@annotation(tech.chowyijiu.huhubot.core.annotation.RuleCheck) || @annotation(tech.chowyijiu.huhubot.core.annotation.CoolDown)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && args(event)", argNames = "joinPoint,event")
    public Object doAround(ProceedingJoinPoint joinPoint, Event event) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handlerName = signature.getDeclaringTypeName() + "." + signature.getName();
        log.info("CheckAspect-Around, Event:{}, Handler:{}", event, handlerName);
        Method method = signature.getMethod();
        //校验规则
        if (method.isAnnotationPresent(RuleCheck.class) &&
                !method.getAnnotation(RuleCheck.class).rule().getRule().check(event)) {
            log.info("Rule mismatch, this call will be intercepted, Event:{}, Handler:{}", event, handlerName);
            return null;
        }
        //校验cd
        if (method.isAnnotationPresent(CoolDown.class)) {
            CoolDown cd = method.getAnnotation(CoolDown.class);
            // 获取id
            JSONObject jsonObject = event.getEventJsonObject();
            Long groupId = jsonObject.getLong("group_id");
            Long id = groupId != null ? groupId : jsonObject.getLong("user_id");
            if (TimeLimiter.limiting(handlerName + id, cd.seconds())) {
                log.info("Cooling down, this call will be intercepted, Event:{}, Handler:{}", event, handlerName);
                return null;
            }
        }
        //log.info("Check passed, this call will continue, Event:{}, Handler:{}", event, handlerName);
        return joinPoint.proceed();
    }


}

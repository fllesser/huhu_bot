package com.github.huhubot.core.aop;

import com.alibaba.fastjson2.JSONObject;
import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.PrivateMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.notice.NoticeEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.github.huhubot.core.annotation.CoolDown;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.core.utils.CoolDownLimiter;

import java.lang.reflect.Method;

/**
 * @author elastic chow
 * @date 2/7/2023
 */

@Slf4j
@Component
@Aspect
public class RuleAndCDAspect {

    @Pointcut("@annotation(com.github.huhubot.core.annotation.RuleCheck) || @annotation(com.github.huhubot.core.annotation.CoolDown)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && args(event)", argNames = "joinPoint,event")
    public Object doAround(ProceedingJoinPoint joinPoint, Event event) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handlerName = signature.getDeclaringTypeName() + "." + signature.getName();
        //log.info("RuleAndCDChecker-Around, Event:{}, Handler:{}", event, handlerName);
        Method method = signature.getMethod();
        //校验规则
        if (method.isAnnotationPresent(RuleCheck.class) &&
                !method.getAnnotation(RuleCheck.class).rule().getRule().check(event)) {
            //log.info("Rule mismatch, this call will be intercepted, Event:{}, Handler:{}", event, handlerName);
            return null;
        }
        //校验 cd
        if (method.isAnnotationPresent(CoolDown.class)) {
            CoolDown cd = method.getAnnotation(CoolDown.class);
            // 获取id
            Long id = 0L;
            if (event instanceof GroupMessageEvent gme) {
                id = gme.getGroupId();
            } else if (event instanceof PrivateMessageEvent pme) {
                id = pme.getUserId();
            }
            if (CoolDownLimiter.check(handlerName + id, cd.seconds())) {
                return null;
            }
        }
        //log.info("{}Check passed, Event:{}, Handler:{}{}", ANSI.YELLOW, event, handlerName, ANSI.RESET);
        return joinPoint.proceed();
    }


}

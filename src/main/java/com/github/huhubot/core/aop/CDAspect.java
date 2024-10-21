package com.github.huhubot.core.aop;

import com.github.huhubot.adapters.onebot.v11.event.Event;
import com.github.huhubot.adapters.onebot.v11.event.message.GroupMessageEvent;
import com.github.huhubot.adapters.onebot.v11.event.message.PrivateMessageEvent;
import com.github.huhubot.core.annotation.CoolDown;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.core.utils.CoolDownLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Slf4j
@Component
@Aspect
public class CDAspect {

    @Pointcut("@annotation(com.github.huhubot.core.annotation.CoolDown)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && args(event)", argNames = "joinPoint,event")
    public Object doAround(ProceedingJoinPoint joinPoint, Event event) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handlerName = signature.getDeclaringTypeName() + "." + signature.getName();
        Method method = signature.getMethod();
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
        return joinPoint.proceed();
    }
}

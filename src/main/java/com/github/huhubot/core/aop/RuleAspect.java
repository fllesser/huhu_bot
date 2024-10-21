package com.github.huhubot.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.github.huhubot.core.annotation.RuleCheck;
import com.github.huhubot.adapters.onebot.v11.event.Event;

import java.lang.reflect.Method;

/**
 * @author elastic chow
 * {@code @date} 2/7/2023
 */

@Slf4j
@Component
@Aspect
public class RuleAspect {

    @Pointcut("@annotation(com.github.huhubot.core.annotation.RuleCheck)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && args(event)", argNames = "joinPoint,event")
    public Object doAround(ProceedingJoinPoint joinPoint, Event event) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //校验规则
        if (method.isAnnotationPresent(RuleCheck.class) && !method.getAnnotation(RuleCheck.class).rule().getRule().check(event)) {
            return null;
        }
        return joinPoint.proceed();
    }


}

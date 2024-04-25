package tech.flless.huhubot.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author FLLess7
 * @date 24/4/2024
 */
@Aspect
@Slf4j
public class HuhuLogAspect {

    @Pointcut("@annotation(tech.flless.huhubot.core.annotation.HuhuLog)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object result = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.info("[Error]");
        }
        return result;
    }
}



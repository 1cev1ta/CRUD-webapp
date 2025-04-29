package com.bsdev.crud_webapp.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("@annotation(com.bsdev.crud_webapp.aspect.annotation.BeforeLog)")
    public void beforeAdvice(JoinPoint joinPoint) {
        log.info("Был вызван метод: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(
            pointcut = "@annotation(com.bsdev.crud_webapp.aspect.annotation.AfterReturningLog)",
            returning = "result"
    )
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        log.info("Был вызван метод {}, с результатом работы: {}",
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(
            pointcut = "@annotation(com.bsdev.crud_webapp.aspect.annotation.AfterThrowingLog)",
            throwing = "exception"
    )
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
        log.error("Произошло исключение {} в методе: {}", exception.getClass(), joinPoint.getSignature().getName());
    }

    @Around("@annotation(com.bsdev.crud_webapp.aspect.annotation.AroundLog)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();

        long end = System.currentTimeMillis();
        log.info("Время выполнения: {}", end - start + " миллисекунд(ы)");
        return result;
    }
}

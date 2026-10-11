package com.tms.profilemanager.app.config.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RunAsSystemUserAspect {

    SecurityManager securityManager;

    @Around("@annotation(com.tms.profilemanager.app.config.security.RunAsSystemUser)")
    public Object processRunningAsSystemUser(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return securityManager.runAsSystemUser(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable t) {
                    throw new ExceptionTransfer(t);
                }
            });
        } catch (ExceptionTransfer t) {
            throw t.getThrowable();
        }
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class ExceptionTransfer extends RuntimeException {
        Throwable throwable;
    }
}

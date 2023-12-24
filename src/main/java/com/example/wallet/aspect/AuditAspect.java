package com.example.wallet.aspect;

import com.example.wallet.model.AuditLog;
import com.example.wallet.repository.AuditRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditRepository auditRepository;

    @Pointcut("execution(* com.example.wallet.controller.UserController.*(..)) || " +
            "execution(* com.example.wallet.controller.AdminController.*(..)) || " +
            "execution(* com.example.wallet.controller.VendorController.*(..))"
    )
    public void auditPointcut() {
    }

    @AfterReturning(pointcut = "auditPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        saveAuditEntry(joinPoint);
    }

    @AfterThrowing(pointcut = "auditPointcut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        saveAuditEntry(joinPoint);
    }

    @Async
    private void saveAuditEntry(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            String methodName = method.getName();
            String parameters = Arrays.toString(joinPoint.getArgs());
            LocalDateTime timestamp = LocalDateTime.now();

            AuditLog auditEntry = new AuditLog();
            auditEntry.setMethodName(methodName);
            auditEntry.setParameters(parameters);
            auditEntry.setTimestamp(timestamp);

            auditRepository.save(auditEntry);
        } catch (Exception e) {
            // Handle exceptions during audit logging
            e.printStackTrace();
        }
    }
}

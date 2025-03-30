/*
package com.gudong.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.service.*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 获取方法返回值类型
        Class<?> returnType = methodSignature.getReturnType();

        System.out.println("Method " + methodName + " called with args: " + Arrays.toString(args));
        System.out.println("Return type: " + returnType.getName());
    }

    @AfterReturning(pointcut = "execution(* com.example.service.*.*(..))", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        // 获取方法返回值类型
        Class<?> returnType = methodSignature.getReturnType();

        System.out.println("Method " + methodName + " returned: " + result);
        System.out.println("Return type: " + returnType.getName());
    }
}
*/

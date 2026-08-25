package dev.sorokin.eventmanager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Pointcuts {

    @Pointcut("execution(* dev.sorokin.eventmanager.user.UserController.*(..))")
    public void userControllerLayer() {}

    @Pointcut("execution(* dev.sorokin.eventmanager.event.EventController.*(..))")
    public void eventControllerLayer() {}
}

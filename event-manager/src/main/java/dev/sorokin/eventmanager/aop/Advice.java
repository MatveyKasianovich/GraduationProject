package dev.sorokin.eventmanager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Advice {

    @Around("dev.sorokin.eventmanager.aop.Pointcuts.userControllerLayer() || dev.sorokin.eventmanager.aop.Pointcuts.eventControllerLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        System.out.println("Вход в " + pjp.getSignature());

        try {
            Object result = pjp.proceed();
            long time = System.currentTimeMillis() - start;
            System.out.println("Успешный выход, время: " + time + "ms");
            return result;
        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            System.out.println("Возникло исключение: " + e.getMessage() + ", время до ошибки: " + time + "ms");
            throw e;
        }
    }
}

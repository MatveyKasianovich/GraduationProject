package dev.sorokin.eventmanager.aop;

import dev.sorokin.eventmanager.kafka.KafkaSender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class Advice {

    private static final Logger log = LoggerFactory.getLogger(Advice.class);

    @Around("dev.sorokin.eventmanager.aop.Pointcuts.userControllerLayer() || dev.sorokin.eventmanager.aop.Pointcuts.eventControllerLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {

        long start = System.currentTimeMillis();
        log.info("Вход в " + pjp.getSignature());

        try {
            Object result = pjp.proceed();
            long time = System.currentTimeMillis() - start;
            log.info("Успешный выход, время: " + time + "ms");
            return result;
        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            log.error("Возникло исключение: " + e.getMessage() + ", время до ошибки: " + time + "ms");
            throw e;
        }
    }
}

package mainpackage;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AspectLogging {

    @Pointcut("execution(public * mainpackage.service.*.findAll(..))")
    public void getAll() {}
    @Pointcut("execution(public * mainpackage.service.*.findAllById(..))")
    public void getById() {}
    @Pointcut("execution(public * mainpackage.service.*.save(..))")
    public void save() {}
    @Pointcut("execution(public * mainpackage.kafka.KafkaConsumer.*(..))")
    public void kafkaReceive() {}



    @Before("execution(public * mainpackage.service.PersonService.findAll())")
    public void logGetAll(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("Получение всех сущностей из метода: {}", method);
    }

    @Before("getById()")
    public void logGetById(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("Получение всех сущности по id из метода: {}", method);
    }

    @AfterReturning("kafkaReceive()")
    public void logKafka(JoinPoint joinPoint) {
        log.info("Обработка kafkaConsumer метода {}", joinPoint.getSignature().toShortString());
    }

    @Around("save()")
    public Object logAppoint(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            result = joinPoint.proceed();
            String method = joinPoint.getSignature().toShortString();
            log.info("Сохранение сущности из методе: {}", method);
        } catch (Throwable e) {
            log.error("Ошибка созранения");
            throw new RuntimeException(e);
        }
        return result;
    }

}

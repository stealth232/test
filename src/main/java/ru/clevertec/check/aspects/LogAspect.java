package ru.clevertec.check.aspects;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.clevertec.check.annotations.log.LogLevel;
import ru.clevertec.check.annotations.log.LogMe;
import static ru.clevertec.check.service.CheckConstants.EMPTY;
import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
@Component
@Aspect
public class LogAspect {
    private final Gson gson;

    @Pointcut("@annotation(ru.clevertec.check.annotations.log.LogMe)")
    private void methodToBeLogged() {
    }

    @AfterReturning(pointcut = "methodToBeLogged()", returning = "o")
    public void loginParameters(JoinPoint joinPoint, Object o) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            method = joinPoint.getTarget().getClass()
                    .getDeclaredMethod(methodSignature.getName(), method.getParameterTypes());
        }
        LogMe annotation = method.getAnnotation(LogMe.class);
        LogLevel level = annotation.value();
        String methodName = methodSignature.getDeclaringTypeName();
        String args = EMPTY;
        if (joinPoint.getArgs() != null) {
            args = gson.toJson(joinPoint.getArgs());
        }
        String result = EMPTY;
        if (o != null) {
            result = gson.toJson(o);
        }
//        switch (level) {
//            case ERROR -> log.error("{} args={} result={}", methodName, args, result);
//            case WARN -> log.warn("{} args={} result={}", methodName, args, result);
//            case DEBUG -> log.debug("{} args={} result={}", methodName, args, result);
//            case INFO -> log.info("{} args={} result={}", methodName, args, result);
//            case TRACE -> log.trace("{} args={} result={}", methodName, args, result);
//        }
    }
}
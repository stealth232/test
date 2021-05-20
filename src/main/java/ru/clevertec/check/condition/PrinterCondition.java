package ru.clevertec.check.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static ru.clevertec.check.service.CheckConstants.CLEVERTEC_TEMPLATE;

public class PrinterCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getResourceLoader().getResource(CLEVERTEC_TEMPLATE).exists();
    }
}

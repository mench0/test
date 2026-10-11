package com.tms.profilemanager.app.entity.audit;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditHandlerFactory {

    private final List<EntityMapper<?>> handlers;

    public AuditHandlerFactory(List<EntityMapper<?>> handlers) {
        this.handlers = handlers;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMapper<T> getHandler(Class<?> entityClass) {
        return (EntityMapper<T>) handlers.stream()
                .filter(h -> h.getEntityClass().equals(entityClass))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No EntityHandler found for " + entityClass.getSimpleName()));
    }
}

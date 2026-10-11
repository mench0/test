package com.tms.profilemanager.app.entity.event;

public record EntityEvent<T>(Class<T> entityClass, Long entityId, OperationType operationType) {
    public enum OperationType {
        INSERT, UPDATE, DELETE
    }
}

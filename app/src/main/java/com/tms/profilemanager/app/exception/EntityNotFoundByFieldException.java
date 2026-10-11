package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityNotFoundByFieldException extends RuntimeException {

    Class<?> entityClass;

    String fieldName;

    Set<Object> values;

    public EntityNotFoundByFieldException(Class<?> entityClass, String fieldName, Object value) {
        this(entityClass, fieldName, Set.of(value));
    }

    public EntityNotFoundByFieldException(Class<?> entityClass, String fieldName, Set<Object> values) {
        this.entityClass = entityClass;
        this.fieldName = fieldName;
        this.values = values;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(MessageSourceUtils.getEntityName(entityClass).orElse(entityClass.getSimpleName()));
        args.add(fieldName);
        if (values.size() > 1) {
            args.add(values.stream().map(Object::toString).collect(joining(", ")));
            return MessageSourceUtils.getMessageOrElseCode(getErrorCode(), args);
        }
        args.add(values.iterator().next());
        return MessageSourceUtils.getMessageOrElseCode(getErrorCode(), args);
    }

    public String getErrorCode() {
        return values.size() > 1 ? "validation.entities-not-found" : "validation.entity-not-found";
    }
}

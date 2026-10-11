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
public class EntityNotFoundByIdException extends RuntimeException {

    Class<?> entityClass;

    Set<Long> ids;

    public EntityNotFoundByIdException(Class<?> entityClass, Long id) {
        this(entityClass, Set.of(id));
    }

    public EntityNotFoundByIdException(Class<?> entityClass, Set<Long> ids) {
        this.entityClass = entityClass;
        this.ids = ids;
    }

    @Override
    public String getMessage() {
        List<Object> args = new ArrayList<>();
        args.add(MessageSourceUtils.getEntityName(entityClass).orElse(entityClass.getSimpleName()));
        if (ids.size() > 1) {
            args.add(ids.stream().map(Object::toString).collect(joining(", ")));
            return MessageSourceUtils.getMessageOrElseCode(getErrorCode(), args);
        }
        args.add(ids.iterator().next());
        return MessageSourceUtils.getMessageOrElseCode(getErrorCode(), args);
    }

    public String getErrorCode() {
        return ids.size() > 1 ? "validation.entities-not-found-by-id" : "validation.entity-not-found-by-id";
    }
}

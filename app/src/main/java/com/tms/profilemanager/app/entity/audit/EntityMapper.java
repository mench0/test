package com.tms.profilemanager.app.entity.audit;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface EntityMapper<T> {

    String DASH = "—";

    Map<String, Object> toMap(T entity);

    default <E> String getReferenceName(EntityManager em, Class<E> clazz, Long id, Function<E, String> mapper) {
        if (id == null) return DASH;
        try {
            E ref = em.getReference(clazz, id);
            return Optional.ofNullable(mapper.apply(ref)).orElse(DASH);
        } catch (javax.persistence.EntityNotFoundException e) {
            return DASH;
        }
    }

    default <E> List<String> getReferenceNames(EntityManager em, Class<E> clazz, List<Long> ids, Function<E, String> mapper) {
        if (ids == null || ids.isEmpty()) return List.of();
        return ids.stream()
                .map(id -> {
                    try {
                        E ref = em.getReference(clazz, id);
                        return mapper.apply(ref);
                    } catch (javax.persistence.EntityNotFoundException e) {
                        return null;
                    }
                })
                .toList();
    }

    Class<T> getEntityClass();

}

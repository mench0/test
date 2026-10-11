package com.tms.profilemanager.app.integration;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.tms.profilemanager.app.utils.MessageSourceUtils.getMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseConstraintsIntegrationTest extends AbstractIntegrationTest {

    @PersistenceContext
    EntityManager entityManager;

    private static final String DATABASE_CONSTRAINT_PREFIX = "database.constraint.";

    @Test
    @Transactional
    @SuppressWarnings("unchecked")
    void allUniqueConstraintsAreLocalizedTest() {
        List<String> notLocalizedConstraints = ((Stream<String>) entityManager.createNativeQuery("select conname from pg_constraint")
                .getResultStream())
                .filter(it -> it.endsWith("_key"))
                .filter(Predicate.not(it -> it.startsWith("migration_execution")))
                .filter(it -> getMessage(DATABASE_CONSTRAINT_PREFIX + it, Locale.ENGLISH).isEmpty()
                        || getMessage(DATABASE_CONSTRAINT_PREFIX + it, Locale.forLanguageTag("ru")).isEmpty())
                .toList();
        assertEquals(List.of(), notLocalizedConstraints);
    }
}

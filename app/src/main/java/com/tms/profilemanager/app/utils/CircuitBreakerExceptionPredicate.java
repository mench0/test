package com.tms.profilemanager.app.utils;

import org.springframework.dao.TransientDataAccessException;

import java.util.function.Predicate;

/**
 * Is used in properties (see application.yml)
 */
public class CircuitBreakerExceptionPredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof TransientDataAccessException) {
            return true;
        }
        while (throwable.getCause() != null) {
            if (throwable instanceof TransientDataAccessException) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
}

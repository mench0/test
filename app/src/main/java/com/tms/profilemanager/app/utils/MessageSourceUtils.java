package com.tms.profilemanager.app.utils;

import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MessageSourceUtils {

    @Setter
    private static MessageSource messageSource;

    public static Optional<String> getMessage(String code) {
        return getMessage(code, LocaleContextHolder.getLocale());
    }

    public static Optional<String> getMessage(String code, Locale locale) {
        try {
            return Optional.of(messageSource.getMessage(code, new Object[]{}, locale));
        } catch (NoSuchMessageException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getMessage(String code, List<Object> params) {
        try {
            return Optional.of(messageSource.getMessage(code, params.toArray(), LocaleContextHolder.getLocale()));
        } catch (NoSuchMessageException e) {
            return Optional.empty();
        }
    }

    public static String getMessageOrElseCode(String code) {
        return getMessage(code).orElse(code);
    }

    public static String getMessageOrElseCode(String code, List<Object> params) {
        return getMessage(code, params).orElse(code);
    }

    public static Optional<String> getEntityName(Class<?> entityClass) {
        return getMessage("entity." + entityClass.getSimpleName() + ".name");
    }
}

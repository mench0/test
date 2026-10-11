package com.tms.profilemanager.app.mapper.filter.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateBuilder {

    BooleanBuilder builder;

    public static PredicateBuilder builder() {
        return new PredicateBuilder(new BooleanBuilder(Expressions.asBoolean(true).isTrue()));
    }

    public static PredicateBuilder builder(Predicate predicate) {
        return new PredicateBuilder(new BooleanBuilder(predicate));
    }

    public static Predicate buildAlwaysFalsePredicate() {
        return new BooleanBuilder(Expressions.asBoolean(true).isFalse());
    }

    public Predicate build() {
        return builder.getValue();
    }

    public <T> PredicateBuilder eq(SimpleExpression<T> attribute, T value) {
        return eq(attribute, value, Objects::nonNull);
    }

    public <T> PredicateBuilder eq(SimpleExpression<T> attribute, T value, Function<T, Boolean> validator) {
        if (validator.apply(value)) {
            builder.and(attribute.eq(value));
        }
        return this;
    }

    public <T> PredicateBuilder in(SimpleExpression<T> attribute, Collection<T> value) {
        return in(attribute, value, v -> !CollectionUtils.isEmpty(v));
    }

    public <T> PredicateBuilder in(SimpleExpression<T> attribute, Collection<T> value, Function<Collection<T>, Boolean> validator) {
        if (validator.apply(value)) {
            builder.and(attribute.in(value));
        }
        return this;
    }

    public <T extends Comparable<?>> PredicateBuilder dateRange(DateTimePath<T> attribute, T start, T end) {
        if (start != null && end != null) {
            builder.and(attribute.between(start, end));
        } else if (start != null) {
            builder.and(attribute.goe(start));
        } else if (end != null) {
            builder.and(attribute.loe(end));
        }
        return this;
    }

    public PredicateBuilder containsIgnoreCase(StringPath attribute, String value) {
        if (Objects.nonNull(value)) {
            builder.and(attribute.containsIgnoreCase(value));
        }
        return this;
    }

    public <T> PredicateBuilder isNull(SimpleExpression<T> attribute) {
        builder.and(attribute.isNull());
        return this;
    }

    public <T> PredicateBuilder isNotNull(SimpleExpression<T> attribute) {
        builder.and(attribute.isNotNull());
        return this;
    }

    public PredicateBuilder isTrue(BooleanExpression booleanExpression) {
        builder.and(booleanExpression);
        return this;
    }

    public PredicateBuilder isFalse(BooleanExpression booleanExpression) {
        builder.and(booleanExpression.not());
        return this;
    }

    public PredicateBuilder and(Predicate predicate) {
        builder.and(predicate);
        return this;
    }

    public PredicateBuilder or(Predicate predicate) {
        builder.or(predicate);
        return this;
    }
}


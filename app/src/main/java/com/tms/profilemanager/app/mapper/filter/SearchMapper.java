package com.tms.profilemanager.app.mapper.filter;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.dto.enums.sort.AvailableSorts;
import com.tms.profilemanager.dto.filter.BaseFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class SearchMapper<F extends BaseFilterDto<?, F>, S extends AvailableSorts<?, S>> {

    @SneakyThrows
    public Predicate toPredicate(Set<Long> ids) {
        F filter = getFilterDtoClass().getDeclaredConstructor().newInstance();
        filter.setIds(ids);
        filter.setArchived(null);
        return toPredicate(filter);
    }

    public Predicate toPredicate(F filter) {
        Predicate accessControlPredicate = getAccessControlPredicate();
        if (accessControlPredicate == null) {
            accessControlPredicate = PredicateBuilder.builder().build();
        }
        Predicate predicate = toPredicateRecursive(filter);
        return PredicateBuilder.builder(predicate).and(accessControlPredicate).build();
    }

    protected Predicate toPredicateRecursive(F filter) {
        if (filter == null) {
            return PredicateBuilder.builder().build();
        }
        Predicate predicate = toPredicateExceptSubFilters(filter);
        if (predicate == null) {
            predicate = PredicateBuilder.builder().build();
        }
        if (CollectionUtils.isEmpty(filter.getSubFiltersAggregatedByOr())) {
            return predicate;
        }
        Predicate subPredicate = filter.getSubFiltersAggregatedByOr().stream()
                .map(this::toPredicateRecursive)
                .reduce((p1, p2) -> PredicateBuilder.builder(p1).or(p2).build())
                .orElse(null);

        return subPredicate != null ? PredicateBuilder.builder(predicate).and(subPredicate).build() : predicate;
    }

    protected abstract Predicate toPredicateExceptSubFilters(F filter);

    public abstract Predicate getAccessControlPredicate();

    @SuppressWarnings("unchecked")
    public Sort toSort(List<? extends SearchRequest.Sort<?>> sorts) {
        if (CollectionUtils.isEmpty(sorts)) {
            return null;
        }
        List<Sort.Order> orders = new ArrayList<>();
        sorts.forEach(sort -> {
            Sort.Order order = toOrder((S) sort.getSortName(), sort.getDirection());
            if (order == null) {
                throw new UnsupportedOperationException("Sort " + sort.getSortName() + " is not supported");
            }
            orders.add(order);
        });
        return orders.isEmpty() ? null : Sort.by(orders);
    }
    
    public Pageable toPageable(SearchRequest.Pagination pagination, Sort sort) {
        if (pagination == null) {
            return null;
        }
        return sort != null
                ? PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort)
                : PageRequest.of(pagination.getPageNumber(), pagination.getPageSize());
    }

    protected abstract Sort.Order toOrder(S sortName, SearchRequest.SortDirection direction);

    protected abstract Class<F> getFilterDtoClass();
}

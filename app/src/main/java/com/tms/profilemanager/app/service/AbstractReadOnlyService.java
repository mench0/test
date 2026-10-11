package com.tms.profilemanager.app.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.mapper.filter.SearchMapper;
import com.tms.profilemanager.dto.filter.BaseFilterDto;
import com.tms.profilemanager.dto.filter.SearchRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.IteratorUtils.toList;

@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractReadOnlyService<E extends BaseEntity, F extends BaseFilterDto<?, F>> {

    Class<E> entityClass;
    QuerydslPredicateExecutor<E> querydslPredicateExecutor;
    SearchMapper<F, ?> searchMapper;

    public Optional<E> getById(@NotNull Long id) {
        return fetchOne(searchMapper.toPredicate(Set.of(id)));
    }

    public E getByIdOrElseThrow(@NotNull Long id) {
        return getById(id).orElseThrow(() -> new EntityNotFoundByFieldException(entityClass, "id", id));
    }

    public void existsByIdOrElseThrow(@NotNull Long id) {
        Predicate predicate = searchMapper.toPredicate(Set.of(id));
        if (!querydslPredicateExecutor.exists(predicate)) {
            throw new EntityNotFoundByFieldException(entityClass, "id", id);
        }
    }

    public List<E> getByIds(@NotEmpty Set<Long> ids) {
        return fetchList(searchMapper.toPredicate(ids), Sort.unsorted());
    }

    public List<E> getByIdsOrElseThrow(@NotEmpty Set<Long> ids) {
        List<E> entities = getByIds(ids);
        if (entities.size() < ids.size()) {
            List<Long> foundIds = entities.stream().map(BaseEntity::getId).toList();
            Set<Long> notFoundIds = ids.stream().filter(id -> !foundIds.contains(id)).collect(toSet());
            throw new EntityNotFoundByFieldException(entityClass, "id", notFoundIds);
        }
        return entities;
    }

    @SuppressWarnings("unchecked")
    public List<E> search(@Valid F filter) {
        return search(SearchRequest.of(filter)).getContent();
    }

    public Optional<E> searchOne(@Valid F filter) {
        List<E> entities = search(filter);
        if (entities.size() > 1) {
            throw new IllegalStateException("Found " + entities.size() + " entities by filter " + filter + ", but expected only 1");
        }
        return entities.stream().findFirst();
    }

    public Page<E> search(@Valid SearchRequest<F, ?> searchRequest) {
        Predicate predicate = searchMapper.toPredicate(searchRequest.getFilter());
        Sort sort = searchMapper.toSort(searchRequest.getSorts());
        Pageable pageable = searchMapper.toPageable(searchRequest.getPagination(), sort);
        if (pageable != null) {
            return fetchPage(predicate, pageable);
        }
        return sort != null ? new PageImpl<>(fetchList(predicate, sort)) : fetchPage(predicate, Pageable.unpaged());
    }

    public long count(@Valid F filterDto) {
        Predicate predicate = searchMapper.toPredicate(filterDto);
        return querydslPredicateExecutor.count(predicate);
    }

    public Optional<E> fetchOne(Predicate predicate) {
        JPAQuery<E> query = createQuery(predicate);
        if (query != null) {
            return ofNullable(query.fetchOne());
        }
        return querydslPredicateExecutor.findOne(predicate);
    }

    protected List<E> fetchList(Predicate predicate, Sort sort) {
        JPAQuery<E> query = createQuery(predicate, sort);
        if (query != null) {
            return query.fetch();
        }
        return toList(querydslPredicateExecutor.findAll(predicate, sort).iterator());
    }

    protected Page<E> fetchPage(Predicate predicate, Pageable pageable) {
        JPAQuery<E> query = createQuery(predicate, pageable);
        if (query != null) {
            List<E> entities = query.fetch();
            return new PageImpl<>(entities, pageable, querydslPredicateExecutor.count(predicate));
        }
        return querydslPredicateExecutor.findAll(predicate, pageable);
    }

    final protected JPAQuery<E> createQuery(Predicate predicate) {
        return createQuery(predicate, Sort.unsorted(), null, null);
    }

    final protected JPAQuery<E> createQuery(Predicate predicate, Pageable pageable) {
        pageable = pageable != null ? pageable : Pageable.unpaged();
        return createQuery(predicate, pageable.getSort(), pageable.isPaged() ? pageable.getPageNumber() : null,
                pageable.isPaged() ? pageable.getPageSize() : null);
    }

    final protected JPAQuery<E> createQuery(Predicate predicate, Sort sort) {
        return createQuery(predicate, sort != null ? sort : Sort.unsorted(), null, null);
    }

    protected JPAQuery<E> createQuery(Predicate predicate, Sort sort, Integer pageNumber, Integer pageSize) {
        return null; // implement this method if you need custom query (e.g. with custom joins)
    }

    protected void applySort(JPQLQuery<?> query, Sort sort, EntityManager entityManager) {
        Querydsl querydsl = new Querydsl(entityManager, new PathBuilderFactory().create(entityClass));
        querydsl.applySorting(sort, query);
    }

    protected void applyPageable(JPQLQuery<?> query, Pageable pageable, EntityManager entityManager) {
        Querydsl querydsl = new Querydsl(entityManager, new PathBuilderFactory().create(entityClass));
        querydsl.applyPagination(pageable, query);
    }
}

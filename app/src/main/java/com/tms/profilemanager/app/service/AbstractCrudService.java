package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.entity.ArchivingBaseEntity;
import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.event.EntityEvent;
import com.tms.profilemanager.app.entity.event.EntityEvent.OperationType;
import com.tms.profilemanager.app.mapper.filter.SearchMapper;
import com.tms.profilemanager.app.service.provider.EntityProvider;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.filter.BaseFilterDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.throwAccessDeniedException;

@Validated
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractCrudService<E extends BaseEntity, C extends BaseCreateDto<?>, P extends BasePatchDto<?>, F extends BaseFilterDto<?, F>>
        extends AbstractReadOnlyService<E, F> {

    final EntityProvider<E, C, P> entityProvider;
    final JpaRepository<E, Long> repository;
    final ApplicationEventPublisher eventPublisher;

    @Setter
    @PersistenceContext
    EntityManager entityManager;

    protected AbstractCrudService(Class<E> entityClass,
                                  EntityProvider<E, C, P> entityProvider,
                                  JpaRepository<E, Long> jpaRepository,
                                  QuerydslPredicateExecutor<E> querydslPredicateExecutor,
                                  SearchMapper<F, ?> searchMapper,
                                  ApplicationEventPublisher eventPublisher) {
        super(entityClass, querydslPredicateExecutor, searchMapper);
        this.entityProvider = entityProvider;
        this.repository = jpaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public E create(@Valid C createDto) {
        assertWriteAccess();
        E entity = entityProvider.create(createDto);
        processEntityBeforeSaving(createDto, entity);
        entity = repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), OperationType.INSERT));
        return entity;
    }

    protected void processEntityBeforeSaving(C createDto, E entity) {
    }

    @Transactional
    public E patch(@Valid P patchDto) {
        E entity = getByIdOrElseThrow(patchDto.getId());
        assertWriteAccess(patchDto.getId());
        entityProvider.patch(patchDto, entity);
        processEntityBeforeSaving(patchDto, entity);
        entity = repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), OperationType.UPDATE));
        return entity;
    }

    protected void processEntityBeforeSaving(P patchDto, E entity) {
    }

    @Transactional
    public void archive(Long id) {
        archive(Set.of(id));
    }

    @Transactional
    public void archive(Set<Long> ids) {
        List<E> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(ids);
        entities.forEach(entity -> {
            if (entity instanceof ArchivingBaseEntity archivingEntity) {
                archivingEntity.setArchived(true);
                archivingEntity.setLastArchivedAt(ClockUtils.now());
            } else {
                throw new UnsupportedOperationException("Entity doesn't support archive operation");
            }
        });
        repository.saveAllAndFlush(entities).forEach(entity ->
                eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), OperationType.UPDATE)));
    }

    @Transactional
    public void restore(Long id) {
        restore(Set.of(id));
    }

    @Transactional
    public void restore(Set<Long> ids) {
        List<E> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(ids);
        entities.forEach(entity -> {
            if (entity instanceof ArchivingBaseEntity archivingEntity) {
                archivingEntity.setArchived(false);
                archivingEntity.setLastRestoredAt(ClockUtils.now());
            } else {
                throw new UnsupportedOperationException("Entity doesn't support restore operation");
            }
        });
        repository.saveAllAndFlush(entities).forEach(entity ->
                eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), OperationType.UPDATE)));
    }

    @Transactional
    public void delete(Long id) {
        delete(Set.of(id));
    }

    @Transactional
    public void delete(Set<Long> ids) {
        List<E> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(ids);
        repository.deleteAll(entities);
        entities.forEach(entity ->
                eventPublisher.publishEvent(new EntityEvent<>(entityClass, entity.getId(), OperationType.DELETE)));
    }

    protected final void assertWriteAccess() {
        if (!hasWriteAccess()) {
            throwAccessDeniedException();
        }
    }

    protected final void assertWriteAccess(Long id) {
        if (!hasWriteAccess(id)) {
            throwAccessDeniedException();
        }
    }

    protected final void assertWriteAccess(Collection<Long> ids) {
        if (!hasWriteAccess(ids)) {
            throwAccessDeniedException();
        }
    }

    protected abstract boolean hasWriteAccess();

    protected boolean hasWriteAccess(Long id) {
        return hasWriteAccess();
    }

    protected final boolean hasWriteAccess(Collection<Long> ids) {
        return ids.stream().anyMatch(this::hasWriteAccess);
    }
}

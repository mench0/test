package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.exception.CustomValidationException;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.patch.BasePatchDto;
import com.tms.profilemanager.dto.patch.BasePatchDto.SetPatch;
import lombok.SneakyThrows;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;


public abstract class EntityProvider<E extends BaseEntity, C extends BaseCreateDto<?>, P extends BasePatchDto<?>> {

    ThreadLocal<CustomValidationException> validationException = new ThreadLocal<>();

    @SneakyThrows
    public final E create(C createDto) {
        validationException.set(null);
        E entity = getEntityClass().getDeclaredConstructor().newInstance();
        initializeEntity(createDto, entity);
        if (validationException.get() != null) {
            throw validationException.get();
        }
        return entity;
    }

    protected abstract void initializeEntity(C createDto, @MappingTarget E entity);

    public final void patch(P patchDto, E entity) {
        validationException.set(null);
        patchEntity(patchDto, entity);
        if (validationException.get() != null) {
            throw validationException.get();
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    protected abstract void patchEntity(P patchDto, @MappingTarget E entity);

    protected abstract Class<E> getEntityClass();

    protected <T> void patchSet(SetPatch<T> setPatch, Set<T> set) {
        switch (setPatch.getPatchType()) {
            case REPLACE -> {
                set.clear();
                set.addAll(setPatch.getValues());
            }
            case ADD -> set.addAll(setPatch.getValues());
            case REMOVE -> set.removeAll(setPatch.getValues());
        }
    }

    protected void addValidationError(String field, String errorCode, Object... args) {
        if (validationException.get() == null) {
            validationException.set(new CustomValidationException());
        }
        validationException.get().addValidationError(field, errorCode, args);
    }
}

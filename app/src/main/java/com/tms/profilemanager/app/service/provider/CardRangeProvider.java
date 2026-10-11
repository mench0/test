package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import com.tms.profilemanager.dto.patch.CardRangePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class CardRangeProvider extends EntityProvider<CardRange, CardRangeCreateDto, CardRangePatchDto> {

    @Autowired @Lazy AcquirerService acquirerService;
    @Autowired @Lazy PaymentSystemService paymentSystemService;
    @Autowired @Lazy
    GeneralLimitService limitService;

    @Override
    @Mapping(target = "limits", ignore = true)
    public abstract void initializeEntity(CardRangeCreateDto source, @MappingTarget CardRange target);

    @AfterMapping
    protected void afterInitializeEntity(CardRangeCreateDto source, @MappingTarget CardRange target) {
        acquirerService.existsByIdOrElseThrow(source.getAcquirerId());
        target.setAcquirerId(source.getAcquirerId());
        target.setLimits(new HashSet<>(limitService.getByIds(source.getLimits())));
    }

    @Override
    @Mapping(target = "limits", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(CardRangePatchDto patchDto, @MappingTarget CardRange entity);

    @AfterMapping
    protected void afterPatchEntity(CardRangePatchDto source, @MappingTarget CardRange target) {
        if (source.getAcquirerId() != null) {
            target.setAcquirerId(source.getAcquirerId()
                    .map(id -> {
                        acquirerService.existsByIdOrElseThrow(id);
                        return id;
                    })
                    .orElse(null));
        }
        target.setLimits(limitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(source.getLimits()));
    }

    @Override
    protected Class<CardRange> getEntityClass() {
        return CardRange.class;
    }
}

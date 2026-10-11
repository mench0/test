package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.service.GeneralLimitService;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.view.CardRangeView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = {GeneralLimitMapper.class})
public abstract class CardRangeMapper implements EntityMapper<CardRange, CardRangeDto> {

    @Autowired @Lazy GeneralLimitService generalLimitService;
    @Autowired @Lazy GeneralLimitMapper generalLimitMapper;

    @Override
    @Mapping(target = "limits", ignore = true)
    public abstract CardRangeDto toDto(CardRange entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget CardRangeDto.CardRangeDtoBuilder<?, ?> dto, CardRange entity) {
        dto.limits(new HashSet<>(generalLimitMapper.toDtoList(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(entity.getLimits().stream()
                .map(BaseEntity::getId).collect(toSet())).stream().toList())));
    }

    public abstract CardRangeView toViewDto(CardRange entity);

    @AfterMapping
    protected void afterToViewDto(@MappingTarget CardRangeView.CardRangeViewBuilder <?, ?> dto, CardRange entity) {
        List<GeneralLimitDto> generalLimitDtos = generalLimitMapper.toDtoList(generalLimitService.getAllByIdsLimitsUsingCacheWithoutAccessControl(entity.getLimits().stream()
                .map(BaseEntity::getId).collect(toSet())).stream().toList());
        if(!generalLimitDtos.isEmpty()) {
            dto.contactlessLimit(generalLimitDtos.get(0).getContactlessLimit());
            dto.contactLimit(generalLimitDtos.get(0).getContactLimit());
        }
    }
}

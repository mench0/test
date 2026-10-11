package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.CardTypeService;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import com.tms.profilemanager.dto.patch.dictionary.CardCodePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class CardCodeProvider extends EntityProvider<CardCode, CardCodeCreateDto, CardCodePatchDto>{

    @Autowired
    @Lazy
    CardTypeService cardTypeService;

    @Override
    public abstract void initializeEntity(CardCodeCreateDto source, @MappingTarget CardCode target);

    @AfterMapping
    protected void afterInitializeEntity(CardCodeCreateDto source, @MappingTarget CardCode target) {
        target.setCardType(cardTypeService.getByIdOrElseThrow(source.getCardTypeId()));
    }

    @Override
    @Mapping(target = "cardTypeId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(CardCodePatchDto patchDto, @MappingTarget CardCode entity);

    @AfterMapping
    protected void afterPatchEntity(CardCodePatchDto patchDto, @MappingTarget CardCode entity) {
        if (patchDto.getCardTypeId() != null) {
            entity.setCardType(cardTypeService.getByIdOrElseThrow(patchDto.getCardTypeId().get()));
        }
    }

    @Override
    protected Class<CardCode> getEntityClass() {
        return CardCode.class;
    }
}

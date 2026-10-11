package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.CardTypeService;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import com.tms.profilemanager.dto.patch.dictionary.CardBinRangePatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class CardBinRangeProvider extends EntityProvider<CardBinRange, CardBinRangeCreateDto, CardBinRangePatchDto>{

    @Autowired
    @Lazy
    CardTypeService cardTypeService;

    @Override
    public abstract void initializeEntity(CardBinRangeCreateDto source, @MappingTarget CardBinRange target);

    @AfterMapping
    protected void afterInitializeEntity(CardBinRangeCreateDto source, @MappingTarget CardBinRange target) {
        target.setCardType(cardTypeService.getByIdOrElseThrow(source.getCardTypeId()));
    }

    @Override
    @Mapping(target = "cardTypeId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(CardBinRangePatchDto patchDto, @MappingTarget CardBinRange entity);

    @AfterMapping
    protected void afterPatchEntity(CardBinRangePatchDto patchDto, @MappingTarget CardBinRange entity) {
        if (patchDto.getCardTypeId() != null) {
            entity.setCardType(cardTypeService.getByIdOrElseThrow(patchDto.getCardTypeId().get()));
        }
    }

    @Override
    protected Class<CardBinRange> getEntityClass() {
        return CardBinRange.class;
    }
}

package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import com.tms.profilemanager.dto.patch.dictionary.CardTypePatchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class CardTypeProvider extends EntityProvider<CardType, CardTypeCreateDto, CardTypePatchDto>{

    @Override
    protected Class<CardType> getEntityClass() {
        return CardType.class;
    }
}

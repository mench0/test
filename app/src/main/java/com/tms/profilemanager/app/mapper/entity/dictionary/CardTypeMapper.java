package com.tms.profilemanager.app.mapper.entity.dictionary;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.dto.CardTypeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CardTypeMapper implements EntityMapper<CardType, CardTypeDto> {
}

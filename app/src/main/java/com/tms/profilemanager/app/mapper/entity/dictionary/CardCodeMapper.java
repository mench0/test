package com.tms.profilemanager.app.mapper.entity.dictionary;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.dto.CardCodeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CardTypeMapper.class)
public abstract class CardCodeMapper implements EntityMapper<CardCode, CardCodeDto> {

}

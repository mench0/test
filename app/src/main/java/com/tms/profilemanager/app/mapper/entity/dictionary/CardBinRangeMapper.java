package com.tms.profilemanager.app.mapper.entity.dictionary;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.dto.CardBinRangeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CardTypeMapper.class)
public abstract class CardBinRangeMapper implements EntityMapper<CardBinRange, CardBinRangeDto> {

}

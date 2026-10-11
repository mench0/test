package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.GeneralLimit;
import com.tms.profilemanager.dto.GeneralLimitDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ContactLimitMapper.class, ContactlessLimitMapper.class})
public abstract class GeneralLimitMapper implements EntityMapper<GeneralLimit, GeneralLimitDto> {
}

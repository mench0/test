package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.dto.ContactlessAppDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DRLMapper.class)
public abstract class ContactlessAppMapper implements EntityMapper<ContactlessApp, ContactlessAppDto> {
}

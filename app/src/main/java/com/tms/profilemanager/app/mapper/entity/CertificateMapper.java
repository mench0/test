package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.Certificate;
import com.tms.profilemanager.dto.CertificateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CertificateMapper implements EntityMapper<Certificate, CertificateDto> {
}

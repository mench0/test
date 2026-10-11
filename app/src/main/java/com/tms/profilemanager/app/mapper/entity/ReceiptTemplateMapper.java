package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.ReceiptTemplate;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.ReceiptTemplateService;
import com.tms.profilemanager.dto.ReceiptTemplateDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class ReceiptTemplateMapper {

    @Lazy
    @Autowired
    ReceiptTemplateService receiptTemplateService;

    @Mapping(target = "logoFileId", ignore = true)
    public abstract ReceiptTemplateDto toDto(ReceiptTemplate entity);

    @AfterMapping
    protected void afterToDto(ReceiptTemplate entity, @MappingTarget ReceiptTemplateDto dto) {
        dto.setLogoFileId(entity.getLogoFileId());
    }

    @Mapping(target = "fontSize", ignore = true)
    abstract ReceiptTemplateDto.HeaderDto headerToHeaderDto(ReceiptTemplate.Header header);

    @AfterMapping
    protected void afterToDto(ReceiptTemplate.Header header, @MappingTarget ReceiptTemplateDto.HeaderDto headerDto) {
        headerDto.setFontSize(Integer.parseInt(header.getFontSize().replace("px", "")));
    }

    @Mapping(target = "fontSize", ignore = true)
    abstract ReceiptTemplateDto.FooterDto footerToFooterDto(ReceiptTemplate.Footer footer);

    @AfterMapping
    protected void afterToDto(ReceiptTemplate.Footer footer, @MappingTarget ReceiptTemplateDto.FooterDto footerDto) {
        footerDto.setFontSize(Integer.parseInt(footer.getFontSize().replace("px", "")));
    }

}

package com.tms.profilemanager.app.mapper.entity;

import com.neovisionaries.i18n.LanguageCode;
import com.tms.profilemanager.dto.LocaleDto;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LocaleMapper {

    public List<LocaleDto> toLocaleDtos() {
        return Arrays.stream(LanguageCode.values())
                .filter(it -> it.getAlpha3().getAlpha2().toString().length() < 3)
                .map(LocaleDto::of)
                .toList();
    }
}

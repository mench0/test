package com.tms.profilemanager.app.mapper.entity;

import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.service.CountryService;
import com.tms.profilemanager.dto.CountryDto;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CountryMapper {

    @Autowired @Lazy
    CountryService countryService;

    public CountryDto toCountryDto(String countryCode) {
        return CountryDto.of(countryService.findCountry(countryCode));
    }

    public List<CountryDto> toListDto(CountryCode[] countryCodes) {
         return Arrays.stream(countryCodes)
                 .filter(it -> it.getNumeric() != -1)
                 .map(CountryDto::of)
                 .distinct()
                 .toList();
    }
}

package com.tms.profilemanager.app.service;

import com.neovisionaries.i18n.CountryCode;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CountryService {

    public CountryCode findCountry(String country) {
        return Arrays.stream(CountryCode.values())
                .filter(it -> it.getName().equals(country))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundByFieldException(CountryCode.class, "countryName", country));
    }
}

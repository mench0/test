package com.tms.profilemanager.app.controller;

import com.neovisionaries.i18n.CurrencyCode;
import com.tms.profilemanager.dto.CurrencyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api/currencies")
@Tag(name = "Currencies", description = "Валюты")
public class CurrencyController {

    @GetMapping
    @Operation(summary = "Получение списка всех валют")
    public Set<CurrencyDto> getAllCurrencies() {
        return Arrays.stream(CurrencyCode.values())
                .filter(currencyCode -> currencyCode.getNumeric() != -1)
                .map(CurrencyDto::of).collect(toSet());
    }
}

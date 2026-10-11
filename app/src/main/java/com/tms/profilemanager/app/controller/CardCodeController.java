package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import com.tms.profilemanager.app.mapper.entity.dictionary.CardCodeMapper;
import com.tms.profilemanager.app.service.CardCodeService;
import com.tms.profilemanager.dto.CardCodeDto;
import com.tms.profilemanager.dto.create.dictionary.CardCodeCreateDto;
import com.tms.profilemanager.dto.enums.sort.CardCodeSort;
import com.tms.profilemanager.dto.filter.CardCodeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardCodePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/card/code")
@Tag(name = "CardCode", description = "Словарь для кодов")
public class CardCodeController extends AbstractCrudController<CardCode, CardCodeDto, CardCodeCreateDto, CardCodePatchDto, CardCodeFilterDto, CardCodeSort> {

    CardCodeService cardCodeService;

    public CardCodeController(CardCodeService service, CardCodeMapper mapper) {
        super(service, mapper);
        this.cardCodeService = service;
    }

    @GetMapping("/grouped-by-types")
    public List<CardCodeDto.CardTypeCodesDto> getMappedCardCodes() {
        return cardCodeService.getCardTypeCodes();
    }

}

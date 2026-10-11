package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import com.tms.profilemanager.app.mapper.entity.dictionary.CardTypeMapper;
import com.tms.profilemanager.app.service.CardTypeService;
import com.tms.profilemanager.dto.CardTypeDto;
import com.tms.profilemanager.dto.create.dictionary.CardTypeCreateDto;
import com.tms.profilemanager.dto.enums.sort.CardTypeSort;
import com.tms.profilemanager.dto.filter.CardTypeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardTypePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards/types")
@Tag(name = "CardType", description = "Типы карт")
public class CardTypeController extends AbstractCrudController
        <CardType, CardTypeDto, CardTypeCreateDto, CardTypePatchDto, CardTypeFilterDto, CardTypeSort> {


    public CardTypeController(CardTypeService service, CardTypeMapper mapper) {
        super(service, mapper);
    }

}

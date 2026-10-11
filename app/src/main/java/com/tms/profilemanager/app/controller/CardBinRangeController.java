package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import com.tms.profilemanager.app.mapper.entity.dictionary.CardBinRangeMapper;
import com.tms.profilemanager.app.service.CardBinRangeService;
import com.tms.profilemanager.dto.CardBinRangeDto;
import com.tms.profilemanager.dto.create.dictionary.CardBinRangeCreateDto;
import com.tms.profilemanager.dto.enums.sort.CardBinRangeSort;
import com.tms.profilemanager.dto.filter.CardBinRangeFilterDto;
import com.tms.profilemanager.dto.patch.dictionary.CardBinRangePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card/bin-range")
@Tag(name = "CardBinRange", description = "Словарь для BIN-диапазонов")
public class CardBinRangeController extends AbstractCrudController
        <CardBinRange, CardBinRangeDto, CardBinRangeCreateDto, CardBinRangePatchDto, CardBinRangeFilterDto, CardBinRangeSort> {


    public CardBinRangeController(CardBinRangeService service, CardBinRangeMapper mapper) {
        super(service, mapper);
    }

}

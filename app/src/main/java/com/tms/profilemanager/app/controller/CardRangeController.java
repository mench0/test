package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.CardRange;
import com.tms.profilemanager.app.mapper.entity.CardRangeMapper;
import com.tms.profilemanager.app.service.CardRangeService;
import com.tms.profilemanager.dto.CardRangeDto;
import com.tms.profilemanager.dto.create.CardRangeCreateDto;
import com.tms.profilemanager.dto.enums.sort.CardRangeSort;
import com.tms.profilemanager.dto.filter.CardRangeFilterDto;
import com.tms.profilemanager.dto.patch.CardRangePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card-range")
@Tag(name = "Card ranges", description = "БИН диапазоны")
public class CardRangeController extends
        AbstractCrudController<CardRange, CardRangeDto, CardRangeCreateDto, CardRangePatchDto, CardRangeFilterDto, CardRangeSort> {

    public CardRangeController(CardRangeService service, CardRangeMapper mapper) {
        super(service, mapper);
    }
}

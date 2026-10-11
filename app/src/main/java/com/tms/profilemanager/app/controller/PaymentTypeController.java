package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.PaymentType;
import com.tms.profilemanager.app.mapper.entity.PaymentTypeMapper;
import com.tms.profilemanager.app.service.PaymentTypeService;
import com.tms.profilemanager.dto.PaymentTypeDto;
import com.tms.profilemanager.dto.create.PaymentTypeCreateDto;
import com.tms.profilemanager.dto.enums.sort.PaymentTypeSort;
import com.tms.profilemanager.dto.filter.PaymentTypeFilterDto;
import com.tms.profilemanager.dto.patch.PaymentTypePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-type")
@Tag(name = "Payment types", description = "Типы платежей")
public class PaymentTypeController extends AbstractCrudController
        <PaymentType, PaymentTypeDto, PaymentTypeCreateDto, PaymentTypePatchDto, PaymentTypeFilterDto, PaymentTypeSort> {

    public PaymentTypeController(PaymentTypeService service, PaymentTypeMapper mapper) {
        super(service, mapper);
    }
}

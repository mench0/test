package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.PaymentSystem;
import com.tms.profilemanager.app.mapper.entity.PaymentSystemMapper;
import com.tms.profilemanager.app.service.PaymentSystemService;
import com.tms.profilemanager.dto.PaymentSystemDto;
import com.tms.profilemanager.dto.create.PaymentSystemCreateDto;
import com.tms.profilemanager.dto.enums.sort.PaymentSystemSort;
import com.tms.profilemanager.dto.filter.PaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.PaymentSystemPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-system")
@Tag(name = "Payment systems", description = "Платежные системы")
public class PaymentSystemController extends AbstractCrudController
        <PaymentSystem, PaymentSystemDto, PaymentSystemCreateDto, PaymentSystemPatchDto, PaymentSystemFilterDto, PaymentSystemSort> {

    public PaymentSystemController(PaymentSystemService service, PaymentSystemMapper mapper) {
        super(service, mapper);
    }
}

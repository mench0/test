package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationPaymentSystemMapper;
import com.tms.profilemanager.app.service.ModificationPaymentSystemService;
import com.tms.profilemanager.dto.modification.ModificationPaymentSystemDto;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.enums.sort.ModificationPaymentSystemSort;
import com.tms.profilemanager.dto.filter.ModificationPaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationPaymentSystemPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modification-payment-system")
@Tag(name = "Modification payment system", description = "Модификации платежной системы")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationPaymentSystemController extends
        AbstractCrudController<ModificationPaymentSystem, ModificationPaymentSystemDto, ModificationPaymentSystemCreateDto, ModificationPaymentSystemPatchDto, ModificationPaymentSystemFilterDto, ModificationPaymentSystemSort> {

    public ModificationPaymentSystemController(ModificationPaymentSystemService service, ModificationPaymentSystemMapper mapper) {
        super(service, mapper);
    }
}

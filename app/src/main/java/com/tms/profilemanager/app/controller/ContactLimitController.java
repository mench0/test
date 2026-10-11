package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.dto.ContactLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.enums.sort.ContactLimitSort;
import com.tms.profilemanager.dto.filter.ContactLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactLimitPatchDto;
import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.mapper.entity.ContactLimitMapper;
import com.tms.profilemanager.app.service.ContactLimitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact-limit")
@Tag(name = "Contact limits", description = "Контактные лимиты")
public class ContactLimitController extends AbstractCrudController
        <ContactLimit, ContactLimitDto, ContactLimitCreateDto, ContactLimitPatchDto, ContactLimitFilterDto, ContactLimitSort> {

    public ContactLimitController(ContactLimitService service, ContactLimitMapper mapper) {
        super(service, mapper);
    }
}

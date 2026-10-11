package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.ContactlessLimit;
import com.tms.profilemanager.app.mapper.entity.ContactlessLimitMapper;
import com.tms.profilemanager.app.service.ContactlessLimitService;
import com.tms.profilemanager.dto.ContactlessLimitDto;
import com.tms.profilemanager.dto.create.limit.ContactlessLimitCreateDto;
import com.tms.profilemanager.dto.enums.sort.ContactlessLimitSort;
import com.tms.profilemanager.dto.filter.ContactlessLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessLimitPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contactless-limit")
@Tag(name = "Contactless limits", description = "Бесконтактные лимиты")
public class ContactlessLimitController extends AbstractCrudController
        <ContactlessLimit, ContactlessLimitDto, ContactlessLimitCreateDto, ContactlessLimitPatchDto, ContactlessLimitFilterDto, ContactlessLimitSort> {

    public ContactlessLimitController(ContactlessLimitService service, ContactlessLimitMapper mapper) {
        super(service, mapper);
    }
}

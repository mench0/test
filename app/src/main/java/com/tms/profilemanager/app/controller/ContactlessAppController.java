package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.ContactlessApp;
import com.tms.profilemanager.app.mapper.entity.ContactlessAppMapper;
import com.tms.profilemanager.app.service.ContactlessAppService;
import com.tms.profilemanager.dto.ContactlessAppDto;
import com.tms.profilemanager.dto.create.ContactlessAppCreateDto;
import com.tms.profilemanager.dto.enums.sort.ContactlessAppSort;
import com.tms.profilemanager.dto.filter.ContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.ContactlessAppPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contactless-app")
@Tag(name = "Contactless apps", description = "Бесконтактные приложения")
public class ContactlessAppController extends AbstractCrudController
        <ContactlessApp, ContactlessAppDto, ContactlessAppCreateDto, ContactlessAppPatchDto, ContactlessAppFilterDto, ContactlessAppSort> {

    public ContactlessAppController(ContactlessAppService service, ContactlessAppMapper mapper) {
        super(service, mapper);
    }
}

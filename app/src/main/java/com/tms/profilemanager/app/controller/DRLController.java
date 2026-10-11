package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.DRL;
import com.tms.profilemanager.app.mapper.entity.DRLMapper;
import com.tms.profilemanager.app.service.DRLService;
import com.tms.profilemanager.dto.DRLDto;
import com.tms.profilemanager.dto.create.DRLCreateDto;
import com.tms.profilemanager.dto.enums.sort.DRLSort;
import com.tms.profilemanager.dto.filter.DRLFilterDto;
import com.tms.profilemanager.dto.patch.DRLPatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drl")
@Tag(name = "DRLs")
public class DRLController extends AbstractCrudController<DRL, DRLDto, DRLCreateDto, DRLPatchDto, DRLFilterDto, DRLSort> {

    public DRLController(DRLService service, DRLMapper mapper) {
        super(service, mapper);
    }
}

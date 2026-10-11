package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.entity.Role;
import com.tms.profilemanager.app.mapper.entity.RoleMapper;
import com.tms.profilemanager.app.service.RoleService;
import com.tms.profilemanager.dto.RoleDto;
import com.tms.profilemanager.dto.create.RoleCreateDto;
import com.tms.profilemanager.dto.enums.sort.RoleSort;
import com.tms.profilemanager.dto.filter.RoleFilterDto;
import com.tms.profilemanager.dto.patch.RolePatchDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
@Tag(name = "Roles", description = "Роли пользователей")
public class RoleController extends AbstractCrudController<Role, RoleDto, RoleCreateDto, RolePatchDto, RoleFilterDto, RoleSort> {

    public RoleController(RoleService service, RoleMapper mapper) {
        super(service, mapper);
    }
}

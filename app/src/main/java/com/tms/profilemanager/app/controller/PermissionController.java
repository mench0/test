package com.tms.profilemanager.app.controller;

import com.tms.profilemanager.app.mapper.entity.PermissionMapper;
import com.tms.profilemanager.app.service.PermissionService;
import com.tms.profilemanager.app.service.UserService;
import com.tms.profilemanager.dto.enums.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
@Tag(name = "Permissions", description = "Права доступа")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionMapper permissionMapper;
    UserService userService;
    PermissionService permissionService;

    @Operation(summary = "Получение перечня всех возможных прав доступа")
    @GetMapping
    public List<Permission.Dto> getAll() {
        return Permission.getAllPermissions().stream()
                .map(permissionMapper::toDto)
                .sorted(Comparator.comparing(Permission.Dto::getCode))
                .collect(toList());
    }

    @Operation(summary = "Получение перечня прав доступа текущего пользователя")
    @GetMapping("/current-user")
    public Set<Permission.Dto> getCurrentUserPermissions() {
        return userService.getCurrentUserOrElseThrow().getAllPermissions().stream()
                .map(permissionMapper::toDto)
                .collect(toSet());
    }

    @Operation(summary = "Получение права доступа по его коду")
    @GetMapping("/code/{code}")
    @ApiResponse(responseCode = "204", description = "Право доступа с указанным кодом не найдено", content = @Content(schema = @Schema()))
    public Permission.Dto getByCode(@PathVariable("code") String code) {
        return Permission.valueOfByCode(code)
                .map(permissionMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @Operation(summary = "Получение всех прав доступа с указанием имеется ли у роли и у текущего пользователя")
    @GetMapping("/by-role-id/{id}")
    public Set<Permission.RoleDto> getById(@PathVariable("id") Long roleId) {
        return permissionService.getPermissionsByRoleId(roleId);
    }
}

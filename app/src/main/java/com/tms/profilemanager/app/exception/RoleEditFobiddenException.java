package com.tms.profilemanager.app.exception;

import com.tms.profilemanager.app.utils.MessageSourceUtils;
import com.tms.profilemanager.dto.enums.Permission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleEditFobiddenException extends RuntimeException {

    Set<Permission> permissions;

    @Override
    public String getMessage() {
        String permissionsStr = permissions.stream()
                .map(Permission::getCode)
                .map(MessageSourceUtils::getMessageOrElseCode)
                .collect(Collectors.joining(", "));
        return MessageSourceUtils.getMessageOrElseCode("you_don't_have_the_all_permissions_of_the_editable_role", List.of(permissionsStr));
    }
}

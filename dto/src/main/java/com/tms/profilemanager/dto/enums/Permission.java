package com.tms.profilemanager.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Permission {
    PAYMENT_SYSTEM_READ(Values.PAYMENT_SYSTEM_READ),
    PAYMENT_SYSTEM_WRITE(Values.PAYMENT_SYSTEM_WRITE),
    USER_READ(Values.USER_READ),
    USUAL_USER_WRITE(Values.USUAL_USER_WRITE),
    SYSTEM_USER_WRITE(Values.SYSTEM_USER_WRITE),
    ROLE_READ(Values.ROLE_READ),
    ROLE_WRITE(Values.ROLE_WRITE),
    ACQUIRER_READ(Values.ACQUIRER_READ),
    ACQUIRER_WRITE(Values.ACQUIRER_WRITE),
    MERCHANT_READ(Values.MERCHANT_READ),
    MERCHANT_WRITE(Values.MERCHANT_WRITE),
    TERMINAL_GROUP_READ(Values.TERMINAL_GROUP_READ),
    TERMINAL_GROUP_WRITE(Values.TERMINAL_GROUP_WRITE),
    TERMINAL_READ(Values.TERMINAL_READ),
    TERMINAL_WRITE(Values.TERMINAL_WRITE),
    TERMINAL_AQUIRER_PARAMS_READ(Values.TERMINAL_AQUIRER_PARAMS_READ),
    TERMINAL_AQUIRER_PARAMS_WRITE(Values.TERMINAL_AQUIRER_PARAMS_WRITE),
    PAYMENT_TYPE_READ(Values.PAYMENT_TYPE_READ),
    PAYMENT_TYPE_WRITE(Values.PAYMENT_TYPE_WRITE),
    AUDIT_READ(Values.AUDIT_READ),
    RECEIPT_TEMPLATE_WRITE(Values.RECEIPT_TEMPLATE_WRITE),
    PAYMENT_SCREEN_LOGO_READ(Values.PAYMENT_SCREEN_LOGO_READ),
    LPAYMENT_SCREEN_LOGO_WRITE(Values.PAYMENT_SCREEN_LOGO_WRITE);


    @JsonValue
    String code;

    public interface Values {
        String PAYMENT_SYSTEM_READ = "payment-system.read";
        String PAYMENT_SYSTEM_WRITE = "payment-system.write";
        String USER_READ = "user.read";
        String USUAL_USER_WRITE = "usual-user.write";
        String SYSTEM_USER_WRITE = "system-user.write";
        String ROLE_READ = "role.read";
        String ROLE_WRITE = "role.write";
        String ACQUIRER_READ = "acquirer.read";
        String ACQUIRER_WRITE = "acquirer.write";
        String MERCHANT_READ = "merchant.read";
        String MERCHANT_WRITE = "merchant.write";
        String TERMINAL_GROUP_READ = "terminal-group.read";
        String TERMINAL_GROUP_WRITE = "terminal-group.write";
        String TERMINAL_READ = "terminal.read";
        String TERMINAL_WRITE = "terminal.write";
        String TERMINAL_AQUIRER_PARAMS_READ = "terminal-aquirer-params.read";
        String TERMINAL_AQUIRER_PARAMS_WRITE = "terminal-aquirer-params.write";
        String PAYMENT_TYPE_READ = "payment-type.read";
        String PAYMENT_TYPE_WRITE = "payment-type.write";
        String PAYMENT_SCREEN_LOGO_READ = "payment-screen-logo.read";
        String PAYMENT_SCREEN_LOGO_WRITE = "payment-screen-logo.write";
        String AUDIT_READ = "audit.read";
        String RECEIPT_TEMPLATE_WRITE = "receipt-template.write";
    }

    @Data
    @Builder
    @Schema(name = "PermissionDto")
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Dto {

        @Schema(description = "Код права доступа")
        String code;

        @Schema(description = "Название права доступа")
        String name;
    }

    @Data
    @Builder
    @Schema(name = "PermissionRoleDto")
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class RoleDto {

        @Schema(description = "Код права доступа")
        String code;

        @Schema(description = "Название права доступа")
        String name;

        @Schema(description = "Имеется ли у роли данные права")
        boolean roleHas;

        @Schema(description = "Имеет ли пользователь данные права")
        boolean currentUserHas;
    }

    public static Set<Permission> getAllPermissions() {
        return Set.of(values());
    }

    public static Optional<Permission> valueOfByCode(String code) {
        List<Permission> permissions = stream(values()).filter(it -> it.getCode().equals(code)).toList();
        if (permissions.size() > 1) {
            throw new IllegalStateException("2 permissions with the same code found: "
                    + permissions.stream().map(Objects::toString).collect(Collectors.joining(", ")));
        }
        return permissions.stream().findFirst();
    }

    public static Permission valueOfByCodeOrElseThrow(String code) {
        Optional<Permission> permission = valueOfByCode(code);
        if (permission.isEmpty()) {
            throw new IllegalStateException("Permission with code " + code + " not found");
        }
        return permission.get();
    }

    public static void assertThatAllNamesAreUnique() {
        stream(values()).forEach(it -> valueOfByCodeOrElseThrow(it.getCode()));
    }
}

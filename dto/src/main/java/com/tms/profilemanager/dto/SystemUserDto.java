package com.tms.profilemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemUserDto extends UserDto {

    @Schema(description = "Серийный номер связанного терминала")
    String terminalSerialNumber;

    @Override
    public boolean isSystemUser() {
        return true;
    }
}

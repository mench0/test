package com.tms.profilemanager.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemUserCreateDto extends UserCreateDto {

    @Schema(description = "Серийный номер связанного терминала")
    String terminalSerialNumber;
}

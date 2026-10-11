package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EMVAppPatchDto extends BasePatchDto<EMVAppDto> {

    Optional<@NotBlank @Size(max = 32) String> aid;

    Optional<@NotBlank @Size(max = 6) String> terminalCaps;

    Optional<@NotBlank @Size(max = 10) String> terminalAdditionalCaps;

    Optional<@NotBlank @Size(max = 10) String> tacDefault;

    Optional<@NotBlank @Size(max = 10) String> tacOnline;

    Optional<@NotBlank @Size(max = 10) String> tacDenial;

    Optional<@NotBlank @Size(max = 256) String> tdol;

    Optional<@NotBlank @Size(max = 256) String> ddol;

    Optional<@NotNull Long> paymentSystemId;

    @Schema(description = "Соответствует типу терминала или всем типам, если значение не задано")
    Optional<TerminalType> terminalType;
}

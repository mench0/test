package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.ContactlessAppDto;
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
import java.util.Set;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessAppPatchDto extends BasePatchDto<ContactlessAppDto> {

    Optional<@NotBlank @Size(max = 32) String> aid;

    Optional<@NotBlank @Size(max = 2) String> kernelId;

    Optional<@NotBlank @Size(max = 3) String> kernelConfiguration;

    Optional<@NotBlank @Size(max = 6) String> terminalCaps;

    Optional<@NotBlank @Size(max = 6) String> terminalCapsCVMNotRequired;

    Optional<@NotBlank @Size(max = 10) String> terminalAdditionalCaps;

    Optional<@NotBlank @Size(max = 10) String> tacDefault;

    Optional<@NotBlank @Size(max = 10) String> tacOnline;

    Optional<@NotBlank @Size(max = 10) String> tacDenial;

    Optional<@NotNull Long> paymentSystemId;

    Optional<Set<@NotBlank String>> tags;

    @Schema(description = "Соответствует типу терминала или всем типам, если значение не задано")
    Optional<TerminalType> terminalType;
}

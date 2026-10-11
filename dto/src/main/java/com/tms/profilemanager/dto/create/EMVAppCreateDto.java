package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.EMVAppDto;
import com.tms.profilemanager.dto.enums.TerminalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EMVAppCreateDto implements BaseCreateDto<EMVAppDto> {

    @NotBlank
    @Size(max = 32)
    String aid;

    @NotBlank
    @Size(max = 6)
    String terminalCaps;

    @NotBlank
    @Size(max = 10)
    String terminalAdditionalCaps;

    @NotBlank
    @Size(max = 10)
    String tacDefault;

    @NotBlank
    @Size(max = 10)
    String tacOnline;

    @NotBlank
    @Size(max = 10)
    String tacDenial;

    @NotBlank
    @Size(max = 256)
    String tdol;

    @NotBlank
    @Size(max = 256)
    String ddol;

    @NotNull
    Long paymentSystemId;

    @Schema(description = "Соответствует типу терминала или всем типам, если значение не задано")
    TerminalType terminalType;
}

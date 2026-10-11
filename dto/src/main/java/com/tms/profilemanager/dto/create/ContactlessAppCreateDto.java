package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.ContactlessAppDto;
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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactlessAppCreateDto implements BaseCreateDto<ContactlessAppDto> {

    @NotBlank
    @Size(max = 32)
    String aid;

    @NotBlank
    @Size(max = 2)
    String kernelId;

    @NotBlank
    @Size(max = 3)
    String kernelConfiguration;

    @NotBlank
    @Size(max = 6)
    String terminalCaps;

    @NotBlank
    @Size(max = 6)
    String terminalCapsCVMNotRequired;

    @NotBlank
    @Size(max = 10) // TODO ?
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

    @NotNull
    Long paymentSystemId;

    @Builder.Default
    Set<@NotBlank String> tags = new HashSet<>();

    @Schema(description = "Соответствует типу терминала или всем типам, если значение не задано")
    TerminalType terminalType;
}

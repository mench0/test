package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.TerminalGroupDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalGroupPatchDto extends BasePatchDto<TerminalGroupDto> {

    @Schema(description = "Название группы терминалов")
    Optional<@NotBlank @Size(max = 255) String> name;

    @Schema(description = "Список логотипов")
    List<Long> paymentScreenLogos = new ArrayList<>();

}

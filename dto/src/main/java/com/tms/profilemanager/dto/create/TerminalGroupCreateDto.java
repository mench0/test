package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.TerminalGroupDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class TerminalGroupCreateDto implements BaseCreateDto<TerminalGroupDto> {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Название группы терминалов")
    String name;

    @Positive
    @Schema(description = "Родитель группы")
    Long parentId;

    @ArraySchema(arraySchema = @Schema(description = "Список логотипов"))
    List<@NotNull Long> paymentScreenLogos = new ArrayList<>();;
}

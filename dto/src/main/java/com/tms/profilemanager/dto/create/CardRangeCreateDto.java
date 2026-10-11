package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.CardRangeDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRangeCreateDto implements BaseCreateDto<CardRangeDto> {

    @NotBlank
    @Size(min = 1, max = 19)
    String low;

    @NotBlank
    @Size(min = 1, max = 19)
    String high;

    @NotBlank
    @Size(min = 1, max = 100)
    String name;

    @NotNull
    Long acquirerId;

    @NotEmpty
    @Size(max = 1)
    @ArraySchema(arraySchema = @Schema(description = "Перечень из одного лимита"))
    Set<Long> limits;
}

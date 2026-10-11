package com.tms.profilemanager.dto.patch;

import com.tms.profilemanager.dto.CardRangeDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRangePatchDto extends BasePatchDto<CardRangeDto> {

    Optional<@NotBlank @Size(min = 1, max = 19) String> low;

    Optional<@NotBlank @Size(min = 1, max = 19) String> high;

    Optional<@NotBlank @Size(min = 1, max = 100) String> name;

    Optional<@NotNull Long> acquirerId;

    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Перечень лимитов"))
    Set<Long> limits = new HashSet<>();

}

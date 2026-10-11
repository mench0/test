package com.tms.profilemanager.dto.create;

import com.tms.profilemanager.dto.VendorDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class VendorCreateDto implements BaseCreateDto<VendorDto> {

    @NotNull
    @Size(min = 1, max = 100)
    @Schema(description = "Имя вендора")
    String name;

    @NotEmpty
    @ArraySchema(arraySchema = @Schema(description = "Список моделей"))
    Set<@NotNull VendorModelInputDto> models;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @FieldNameConstants
    public static class VendorModelInputDto {

        Long id;

        @NotNull
        @Size(min = 1, max = 100)
        @Schema(description = "Имя модели")
        String name;

    }
}

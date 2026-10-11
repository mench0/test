package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.MerchantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantFilterDto extends BaseFilterDto<MerchantDto, MerchantFilterDto> {

    @NotBlank
    @Min(3)
    @Schema(description = "Полнотекстовый поиск по названию мерчанта")
    String searchText;
}

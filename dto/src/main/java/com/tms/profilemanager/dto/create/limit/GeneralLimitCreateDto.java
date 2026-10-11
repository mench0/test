package com.tms.profilemanager.dto.create.limit;

import com.tms.profilemanager.dto.GeneralLimitDto;
import com.tms.profilemanager.dto.create.BaseCreateDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class GeneralLimitCreateDto implements BaseCreateDto<GeneralLimitDto> {

    @NotBlank
    @Size(max = 50)
    String name;

    @NotNull
    Long contactLimitId;

    @NotNull
    Long contactlessLimitId;
}

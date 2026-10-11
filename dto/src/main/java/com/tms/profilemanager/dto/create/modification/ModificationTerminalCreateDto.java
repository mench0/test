package com.tms.profilemanager.dto.create.modification;

import com.tms.profilemanager.dto.create.BaseCreateDto;
import com.tms.profilemanager.dto.modification.ModificationTerminalDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ModificationTerminalCreateDto implements BaseCreateDto<ModificationTerminalDto> {

    @Size(max = 100)
    String country;

    @Size(max = 10)
    String servicePin;

    @NotNull
    Long groupId;
}

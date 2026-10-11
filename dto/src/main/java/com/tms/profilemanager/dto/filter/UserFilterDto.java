package com.tms.profilemanager.dto.filter;

import com.tms.profilemanager.dto.UserDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterDto extends BaseFilterDto<UserDto, UserFilterDto> {

    @Singular
    Set<@NotBlank String> usernames;

    @Singular
    Set<@Email String> emails;

    @Singular
    Set<@NotBlank String> terminalSerialNumbers;

    Boolean isSystemUser;
}

package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.UserController;
import com.tms.profilemanager.app.test.utils.request.RequestSender;
import com.tms.profilemanager.dto.SystemUserDto;
import com.tms.profilemanager.dto.create.SystemUserCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemUserGenerator {

    UserController userController;

    @RunAsSystemUser
    public SystemUserDto createAndSave(int number, Long roleId) {
        return userController.create(buildCreateDto(number, roleId));
    }

    public SystemUserCreateDto buildCreateDto(int number, Long roleId) {
        return SystemUserCreateDto.builder()
                .fullName("systemUserFullName" +  number)
                .username("systemUserUsername" + number)
                .roleId(roleId)
                .password(RequestSender.DEFAULT_TEST_PASSWORD)
                .build();
    }
}

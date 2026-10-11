package com.tms.profilemanager.app.test.utils.generator;

import com.tms.profilemanager.app.config.security.RunAsSystemUser;
import com.tms.profilemanager.app.controller.UserController;
import com.tms.profilemanager.app.test.utils.request.RequestSender;
import com.tms.profilemanager.dto.UsualUserDto;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;


@Component
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsualUserGenerator {

    UserController userController;

    @RunAsSystemUser
    public UsualUserDto createAndSave(int number, Long roleId) {
        return userController.create(buildCreateDto(number, roleId));
    }

    public UsualUserCreateDto buildCreateDto(int number, Long roleId) {
        return UsualUserCreateDto.builder()
                .username("username" + number)
                .fullName("fullName" +  number)
                .email("email" + number + "@mail.com")
                .password(RequestSender.DEFAULT_TEST_PASSWORD)
                .roleId(roleId)
                .userSettings(UsualUserCreateDto.UserSettingsCreateDto.builder()
                        .locale(Locale.ENGLISH)
                        .build())
                .build();
    }
}

package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.Token;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.entity.UsualUser;
import com.tms.profilemanager.app.exception.EntityNotFoundByFieldException;
import com.tms.profilemanager.app.mapper.entity.UserMapper;
import com.tms.profilemanager.app.repository.UserRepository;
import com.tms.profilemanager.app.repository.UsualUserRepository;
import com.tms.profilemanager.app.service.provider.UsualUserProvider;
import com.tms.profilemanager.app.utils.EmailSender;
import com.tms.profilemanager.dto.create.UsualUserCreateDto;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import com.tms.profilemanager.dto.patch.UsualUserPatchDto;
import com.tms.profilemanager.dto.request.ChangeUserPasswordRequestDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.tms.profilemanager.app.utils.EmailSender.PASSWORD_GENERATED_TEMPLATE;
import static com.tms.profilemanager.app.utils.EmailSender.RESET_PASSWORD_TEMPLATE;
import static com.tms.profilemanager.app.utils.PasswordGenerator.generatePassword;
import static com.tms.profilemanager.dto.enums.Permission.Values.USUAL_USER_WRITE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsualUserService {

    TokenService tokenService;
    EmailSender emailSender;
    PasswordEncoder passwordEncoder;
    UsualUserProvider usualUserProvider;
    UserRepository userRepository;
    UsualUserRepository usualUserRepository;
    UserService userService;
    UserMapper userMapper;
    SecurityManager securityManager;

    @Value("${app.reset-password-url}")
    String resetPasswordUrl;

    @Transactional(readOnly = true)
    public UsualUser getByIdOrElseThrow(@NotNull Long id) {
        User user = userService.getByIdOrElseThrow(id);
        if (!(user instanceof UsualUser)) {
            throw new EntityNotFoundByFieldException(UsualUser.class, "id", id);
        }
        return (UsualUser) user;
    }

    @Transactional(readOnly = true)
    public UsualUser getActiveUsualUserByEmailOrElseThrow(@NotBlank String email) {
        return getActiveUserByEmail(email).orElseThrow(() -> new EntityNotFoundByFieldException(UsualUser.class, "email", email));
    }

    @Transactional(readOnly = true)
    public Optional<UsualUser> getActiveUserByEmail(@NotBlank String email) {
        return userService.searchOne(UserFilterDto.builder().email(email).build()).map(it -> (UsualUser) it);
    }

    @Transactional(readOnly = true)
    public Optional<UsualUser> getArchivedUserByEmail(@NotBlank String email) {
        return usualUserRepository.findByEmailAndArchived(email, true);
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + USUAL_USER_WRITE + "')")
    public UsualUser create(@Valid UsualUserCreateDto createDto) {
        boolean passwordGenerated = false;
        String password = null;
        if (createDto.getPassword() == null) {
            password = generatePassword();
            passwordGenerated = true;
        } else {
            password = createDto.getPassword();
        }
        createDto.setPassword(password);

        Optional<UsualUser> optionalUser = getArchivedUserByEmail(createDto.getEmail());
        UsualUser user = null;
        if(optionalUser.isEmpty()) {
            user = userRepository.saveAndFlush(usualUserProvider.create(createDto));
        } else {
            UsualUser usualUser = optionalUser.get();
            userService.restore(usualUser.getId());
            usualUser.setPasswordHash(passwordEncoder.encode(password));
            user = patchUsualUser(createDto, usualUser);
        }

        if (passwordGenerated) {
            String content = emailSender.getContent(PASSWORD_GENERATED_TEMPLATE, Map.of(
                    "fullName", user.getFullName(),
                    "password", password
            ));
            emailSender.send(user.getEmail(), "Registration in TMS", content, emailSender.getInlineAttachments());
        }
        return user;
    }

    @Transactional
    public UsualUser patchUsualUser(@Valid UsualUserCreateDto createDto, UsualUser entity) {
        UsualUserPatchDto patchDto = userMapper.usualUserCreateToUsualUserPatch(createDto, entity);
        userService.assertWriteAccess(entity);
        usualUserProvider.patch(patchDto, entity);
        return userRepository.save(entity);
    }

    @Transactional
    public UsualUser patch(@Valid UsualUserPatchDto patchDto) {
        UsualUser entity = getByIdOrElseThrow(patchDto.getId());
        userService.assertWriteAccess(entity);
        usualUserProvider.patch(patchDto, entity);
        return userRepository.save(entity);
    }

    @Transactional
    public void sendPasswordResetLink(@NotBlank String email) {
        UsualUser usualUser = securityManager.runAsSystemUser(() -> getActiveUsualUserByEmailOrElseThrow(email));
        String tokenValue = generateTokenAndSave(usualUser.getId(), Token.TokenType.PASSWORD_RESET);
        String content = emailSender.getContent(RESET_PASSWORD_TEMPLATE, Map.of(
                "fullName", usualUser.getFullName(),
                "link", resetPasswordUrl + "?token=" + tokenValue
        ));
        emailSender.send(usualUser.getEmail(), "Password reset", content, emailSender.getInlineAttachments());
    }

    @Transactional
    public void changePassword(@Valid ChangeUserPasswordRequestDto command) {
        UsualUser usualUser = null;
        boolean credentialsValid = false;
        if (command.getSecretToken() != null) {
            usualUser = tokenService.findToken(command.getSecretToken(), Token.TokenType.PASSWORD_RESET)
                    .map(Token::getUserId)
                    .map(id -> securityManager.runAsSystemUser(() -> getByIdOrElseThrow(id)))
                    .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED));
            credentialsValid = true;
        } else if (command.getUserId() != null && command.getOldPassword() != null) {
            usualUser = securityManager.runAsSystemUser(() -> getByIdOrElseThrow(command.getUserId()));
            if (passwordEncoder.matches(command.getOldPassword(), usualUser.getPasswordHash())) {
                credentialsValid = true;
            }
        }
        if (!credentialsValid) {
            throw new ResponseStatusException(UNAUTHORIZED);
        }
        usualUser.setPasswordHash(passwordEncoder.encode(command.getNewPassword()));
        userRepository.save(usualUser);
        tokenService.deleteByUserId(usualUser.getId());
    }

    private String generateTokenAndSave(Long userId, Token.TokenType tokenType) {
        String token;
        int attempts = 0;
        do {
            if (attempts >= 10) {
                throw new IllegalStateException(attempts + " attempts of generating token failed!");
            }
            token = String.valueOf(Math.abs(new Random().nextLong()));
            attempts += 1;
        } while (tokenService.addToken(userId, token, tokenType) == null);
        return token;
    }
}

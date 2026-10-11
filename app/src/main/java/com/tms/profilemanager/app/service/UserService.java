package com.tms.profilemanager.app.service;

import com.querydsl.core.types.Predicate;
import com.tms.profilemanager.app.entity.QUsualUser;
import com.tms.profilemanager.app.entity.User;
import com.tms.profilemanager.app.mapper.filter.UserSearchMapper;
import com.tms.profilemanager.app.mapper.filter.utils.PredicateBuilder;
import com.tms.profilemanager.app.repository.UserRepository;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.app.utils.ClockUtils;
import com.tms.profilemanager.dto.filter.UserFilterDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.hasPermissionToWrite;
import static com.tms.profilemanager.app.config.security.AuthExpressionsHolder.throwAccessDeniedException;
import static com.tms.profilemanager.app.entity.QUser.user;
import static com.tms.profilemanager.dto.enums.Permission.Values.USER_READ;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService extends AbstractReadOnlyService<User, UserFilterDto> {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserSearchMapper searchMapper,
                       PasswordEncoder passwordEncoder) {
        super(User.class, userRepository, searchMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        return AuthenticationHolder.getCurrentUserId().map(this::getByIdOrElseThrow);
    }

    @Transactional(readOnly = true)
    public User getCurrentUserOrElseThrow() {
        return getCurrentUser().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@auth.isCurrentUser(#username) || hasAuthority('" + USER_READ + "')")
    public Optional<User> getByUsername(@NotBlank String username) {
        return searchOne(UserFilterDto.builder().username(username).build());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@auth.isCurrentUser(#usernameOrEmail) || hasAuthority('" + USER_READ + "')")
    public Optional<User> getByUsernameOrEmail(@NotBlank String usernameOrEmail) {
        return searchOne(UserFilterDto.builder().subFiltersAggregatedByOr(Set.of(
                UserFilterDto.builder().username(usernameOrEmail).build(),
                UserFilterDto.builder().email(usernameOrEmail).build()
        )).build());
    }

    @Transactional(readOnly = true)
    public Optional<User> getByUsernameOrEmail(@NotBlank String usernameOrEmail, @NotBlank String password) {
        Predicate byUsername = PredicateBuilder.builder().eq(user.username, usernameOrEmail).build();
        Predicate byEmail = PredicateBuilder.builder().eq(user.as(QUsualUser.class).email.toLowerCase(), usernameOrEmail.toLowerCase()).build();
        return userRepository.findOne(PredicateBuilder.builder(byUsername).or(byEmail).build())
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    @Transactional
    public void archive(@NotNull Long id) {
        archive(Set.of(id));
    }

    @Transactional
    public void archive(@NotEmpty Set<Long> ids) {
        List<User> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(entities);
        entities.forEach(entity -> {
            entity.setArchived(true);
            entity.setLastArchivedAt(ClockUtils.now());
        });
        userRepository.saveAll(entities);
    }

    @Transactional
    public void restore(@NotNull Long id) {
        restore(Set.of(id));
    }

    @Transactional
    public void restore(@NotEmpty Set<Long> ids) {
        List<User> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(entities);
        entities.forEach(entity -> {
            entity.setArchived(false);
            entity.setLastRestoredAt(ClockUtils.now());
        });
        userRepository.saveAll(entities);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        delete(Set.of(id));
    }

    @Transactional
    public void delete(@NotEmpty Set<Long> ids) {
        List<User> entities = getByIdsOrElseThrow(ids);
        assertWriteAccess(entities);
        userRepository.deleteAll(entities);
    }

    public void assertWriteAccess(User user) {
        if (!hasPermissionToWrite(user)) {
            throwAccessDeniedException();
        }
    }

    public void assertWriteAccess(Collection<User> users) {
        users.forEach(this::assertWriteAccess);
    }
}

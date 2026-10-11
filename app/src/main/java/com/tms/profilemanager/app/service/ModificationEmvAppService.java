package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.modification.ModificationEmvApp;
import com.tms.profilemanager.app.mapper.filter.ModificationEmvAppSearchMapper;
import com.tms.profilemanager.app.repository.ModificationEmvAppRepository;
import com.tms.profilemanager.app.service.provider.modificatiion.ModificationEmvAppProvider;
import com.tms.profilemanager.dto.create.modification.ModificationEmvAppCreateDto;
import com.tms.profilemanager.dto.filter.ModificationEmvAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationEmvAppPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_WRITE;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationEmvAppService extends AbstractCrudService<ModificationEmvApp, ModificationEmvAppCreateDto, ModificationEmvAppPatchDto, ModificationEmvAppFilterDto> {


    protected ModificationEmvAppService(ModificationEmvAppProvider entityProvider,
                                        ModificationEmvAppRepository repository,
                                        ModificationEmvAppSearchMapper searchMapper,
                                        ApplicationEventPublisher eventPublisher) {
        super(ModificationEmvApp.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }
}

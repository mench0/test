package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.modification.ModificationContactlessApp;
import com.tms.profilemanager.app.mapper.filter.ModificationContactlessAppSearchMapper;
import com.tms.profilemanager.app.repository.ModificationContactlessAppRepository;
import com.tms.profilemanager.app.service.provider.modificatiion.ModificationContactlessAppProvider;
import com.tms.profilemanager.dto.create.modification.ModificationContactlessAppCreateDto;
import com.tms.profilemanager.dto.filter.ModificationContactlessAppFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationContactlessAppPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_WRITE;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationContactlessAppService extends AbstractCrudService<ModificationContactlessApp, ModificationContactlessAppCreateDto, ModificationContactlessAppPatchDto, ModificationContactlessAppFilterDto> {


    protected ModificationContactlessAppService(ModificationContactlessAppProvider entityProvider,
                                                ModificationContactlessAppRepository repository,
                                                ModificationContactlessAppSearchMapper searchMapper,
                                                ApplicationEventPublisher eventPublisher) {
        super(ModificationContactlessApp.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }
}

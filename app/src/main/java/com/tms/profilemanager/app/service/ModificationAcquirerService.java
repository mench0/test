package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationAcquirerMapper;
import com.tms.profilemanager.app.mapper.filter.ModificationAcquirerSearchMapper;
import com.tms.profilemanager.app.repository.ModificationAcquirerRepository;
import com.tms.profilemanager.app.service.provider.modificatiion.ModificationAcquirerProvider;
import com.tms.profilemanager.dto.create.modification.ModificationAcquirerCreateDto;
import com.tms.profilemanager.dto.filter.ModificationAcquirerFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationAcquirerPatchDto;
import com.tms.profilemanager.dto.view.TerminalView;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_WRITE;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationAcquirerService extends AbstractCrudService<ModificationAcquirer, ModificationAcquirerCreateDto, ModificationAcquirerPatchDto, ModificationAcquirerFilterDto> {

    ModificationAcquirerRepository modificationRepository;
    ModificationAcquirerMapper modificationAcquirerMapper;

    protected ModificationAcquirerService(ModificationAcquirerProvider entityProvider,
                                          ModificationAcquirerRepository repository,
                                          ModificationAcquirerSearchMapper searchMapper,
                                          ModificationAcquirerRepository modificationRepository,
                                          ModificationAcquirerMapper modificationAcquirerMapper,
                                          ApplicationEventPublisher eventPublisher) {
        super(ModificationAcquirer.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.modificationRepository = modificationRepository;
        this.modificationAcquirerMapper = modificationAcquirerMapper;
    }

    @Transactional(readOnly = true)
    public TerminalView getAcquirerWithModification(TerminalView terminalView, List<Long> groupAndParentIds) {
        List<ModificationAcquirer> modifications = modificationRepository.findAllByGroupIdInOrderByGroupId(groupAndParentIds);
        modificationAcquirerMapper.acquirerModificationMapping(terminalView, modifications, groupAndParentIds);
        return terminalView;
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }
}

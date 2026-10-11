package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationTerminalMapper;
import com.tms.profilemanager.app.mapper.filter.ModificationTerminalSearchMapper;
import com.tms.profilemanager.app.repository.ModificationTerminalRepository;
import com.tms.profilemanager.app.service.provider.modificatiion.ModificationTerminalProvider;
import com.tms.profilemanager.dto.create.modification.ModificationTerminalCreateDto;
import com.tms.profilemanager.dto.filter.ModificationTerminalFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationTerminalPatchDto;
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
public class ModificationTerminalService extends AbstractCrudService<ModificationTerminal, ModificationTerminalCreateDto, ModificationTerminalPatchDto, ModificationTerminalFilterDto> {

    ModificationTerminalRepository repository;
    ModificationTerminalMapper modificationTerminalMapper;

    protected ModificationTerminalService(ModificationTerminalProvider entityProvider,
                                          ModificationTerminalRepository repository,
                                          ModificationTerminalSearchMapper searchMapper,
                                          ModificationTerminalMapper modificationTerminalMapper,
                                          ApplicationEventPublisher eventPublisher) {
        super(ModificationTerminal.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.repository = repository;
        this.modificationTerminalMapper = modificationTerminalMapper;
    }

    @Transactional(readOnly = true)
    public TerminalView getTerminalViewWithModification(TerminalView terminalView, List<Long> groupAndParentIds) {
        List<ModificationTerminal> modifications = repository.findAllByGroupIdInOrderByGroupId(groupAndParentIds);
        return modificationTerminalMapper.terminalViewModificationMapping(terminalView, modifications);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }
}

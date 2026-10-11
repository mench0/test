package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.repository.TerminalAcquirerOperationRepository;
import com.tms.profilemanager.app.utils.AuthenticationHolder;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalAcquirerParamsService {

    TerminalAcquirerOperationRepository terminalAcquirerOperationRepository;

    public TerminalAcquirerParamsService(TerminalAcquirerOperationRepository terminalAcquirerOperationRepository) {
        this.terminalAcquirerOperationRepository = terminalAcquirerOperationRepository;
    }

    @Transactional
    public void removeTerminalAcquirerParamsAvailableOperations(Long acquirerId, AcquirerOperationType availableOperation) {
        terminalAcquirerOperationRepository.deleteByAcquirerIdAndOperation(acquirerId, availableOperation.name());
    }

    @Transactional
    public void addTerminalAcquirerParamsAvailableOperations(Long acquirerId, AcquirerOperationType availableOperation) {
        terminalAcquirerOperationRepository.insertTerminalAcquirerOperations(
                acquirerId,
                availableOperation.name(),
                AuthenticationHolder.getCurrentUserId().orElse(null)
        );
    }
}

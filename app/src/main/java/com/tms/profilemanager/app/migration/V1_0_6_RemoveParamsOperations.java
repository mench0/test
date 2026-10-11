package com.tms.profilemanager.app.migration;

import com.tms.profilemanager.app.config.security.SecurityManager;
import com.tms.profilemanager.app.entity.Acquirer;
import com.tms.profilemanager.app.entity.AcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import com.tms.profilemanager.app.entity.TerminalAcquirerParams;
import com.tms.profilemanager.app.repository.TerminalAcquirerParamsRepository;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.dto.enums.AcquirerOperationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Migration is responsible for removing TerminalAcquirerOperations
 * where their associated Acquirer does not have corresponding AvailableOperations.
 * It ensures data consistency between TerminalAcquirerOperations and Acquirer AvailableOperations.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1_0_6_RemoveParamsOperations implements Migration {

    SecurityManager securityManager;
    TerminalAcquirerParamsRepository repository;
    AcquirerService acquirerService;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public void execute() {
        securityManager.runAsSystemUser(() -> {
                    String hqlQuery = """
                            select tap
                            from TerminalAcquirerParams tap
                            join tap.availableOperations tao
                            where tao.operation not in (
                                select ao.operation
                                from AcquirerOperation ao
                                where ao.acquirer.id = tap.acquirerId
                            )""";

                    List<TerminalAcquirerParams> params = entityManager.createQuery(hqlQuery, TerminalAcquirerParams.class)
                            .getResultList();

                    params.forEach(
                            terminalAcquirerParams -> {
                                Long acquirerId = terminalAcquirerParams.getAcquirerId();
                                Acquirer acquirer = acquirerService.getByIdUsingCacheWithoutAccessControl(acquirerId).orElseThrow();
                                List<AcquirerOperationType> acquirerAvailableOperations =
                                        acquirer.getAvailableOperations().stream()
                                                .map(AcquirerOperation::getOperation)
                                                .toList();
                                terminalAcquirerParams.getAvailableOperations().removeIf(
                                        terminalAcquirerOperation -> !acquirerAvailableOperations.contains(terminalAcquirerOperation.getOperation())
                                );

                                if (terminalAcquirerParams.getAvailableOperations().isEmpty()) {
                                    terminalAcquirerParams.getAvailableOperations().add(
                                            TerminalAcquirerOperation.builder()
                                                    .terminalAcquirerParams(terminalAcquirerParams)
                                                    .operation(acquirerAvailableOperations.get(0))
                                                    .operationOrder(1)
                                                    .build()
                                    );
                                }
                            }
                    );
                    repository.saveAll(params);
                }
        );
    }
}

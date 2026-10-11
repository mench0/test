package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.modification.ModificationPaymentSystem;
import com.tms.profilemanager.app.mapper.entity.modifiication.ModificationPaymentSystemMapper;
import com.tms.profilemanager.app.mapper.filter.ModificationPaymentSystemSearchMapper;
import com.tms.profilemanager.app.repository.ModificationPaymentSystemRepository;
import com.tms.profilemanager.app.service.provider.modificatiion.ModificationPaymentSystemProvider;
import com.tms.profilemanager.dto.create.modification.ModificationPaymentSystemCreateDto;
import com.tms.profilemanager.dto.filter.ModificationPaymentSystemFilterDto;
import com.tms.profilemanager.dto.patch.modification.ModificationPaymentSystemPatchDto;
import com.tms.profilemanager.dto.view.PaymentSystemView;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_WRITE;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModificationPaymentSystemService extends AbstractCrudService<ModificationPaymentSystem, ModificationPaymentSystemCreateDto, ModificationPaymentSystemPatchDto, ModificationPaymentSystemFilterDto> {

    ModificationPaymentSystemRepository modificationRepository;
    ModificationPaymentSystemMapper modificationPaymentSystemMapper;

    protected ModificationPaymentSystemService(ModificationPaymentSystemProvider entityProvider,
                                               ModificationPaymentSystemRepository jpaRepository,
                                               ModificationPaymentSystemSearchMapper searchMapper,
                                               ModificationPaymentSystemRepository modificationRepository,
                                               ModificationPaymentSystemMapper modificationPaymentSystemMapper,
                                               ApplicationEventPublisher eventPublisher) {
        super(ModificationPaymentSystem.class, entityProvider, jpaRepository, jpaRepository, searchMapper, eventPublisher);
        this.modificationRepository = modificationRepository;
        this.modificationPaymentSystemMapper = modificationPaymentSystemMapper;
    }

    @Transactional(readOnly = true)
    public void getPaymentSystemWithModification(List<Long> groupAndParentIds, Set<PaymentSystemView> paymentSystemViews) {
        List<ModificationPaymentSystem> modifications = modificationRepository.findAllByGroupIdInOrderByGroupId(groupAndParentIds);
        modificationPaymentSystemMapper.paymentSystemModificationMapping(modifications, paymentSystemViews);
    }

    @Transactional
    public ModificationPaymentSystem findOrCreateModificationPaymentSystem(Long paymentSystemId, Long groupId) {
        return modificationRepository.findByPaymentSystemIdAndGroupId(paymentSystemId, groupId)
                .orElseGet(() -> create(ModificationPaymentSystemCreateDto.builder()
                        .paymentSystemId(paymentSystemId)
                        .groupId(groupId)
                        .build()));
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }
}

package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.ContactLimit;
import com.tms.profilemanager.app.mapper.filter.ContactLimitSearchMapper;
import com.tms.profilemanager.app.repository.ContactLimitRepository;
import com.tms.profilemanager.app.service.provider.limit.ContactLimitProvider;
import com.tms.profilemanager.dto.create.limit.ContactLimitCreateDto;
import com.tms.profilemanager.dto.filter.ContactLimitFilterDto;
import com.tms.profilemanager.dto.patch.ContactLimitPatchDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.tms.profilemanager.dto.enums.Permission.Values.PAYMENT_SYSTEM_WRITE;

@Service
public class ContactLimitService extends
        AbstractCrudService<ContactLimit, ContactLimitCreateDto, ContactLimitPatchDto, ContactLimitFilterDto> {

    public ContactLimitService(ContactLimitProvider entityProvider,
                               ContactLimitRepository repository,
                               ContactLimitSearchMapper searchMapper,
                               ApplicationEventPublisher eventPublisher) {
        super(ContactLimit.class, entityProvider, repository, repository, searchMapper, eventPublisher);
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(PAYMENT_SYSTEM_WRITE);
    }
}

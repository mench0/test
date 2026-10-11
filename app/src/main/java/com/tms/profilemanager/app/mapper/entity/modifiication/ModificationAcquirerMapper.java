package com.tms.profilemanager.app.mapper.entity.modifiication;

import com.tms.profilemanager.app.entity.modification.ModificationAcquirer;
import com.tms.profilemanager.app.mapper.entity.AcquirerMapper;
import com.tms.profilemanager.app.mapper.entity.EntityMapper;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.AcquirerService;
import com.tms.profilemanager.app.service.ModificationPaymentSystemService;
import com.tms.profilemanager.dto.TerminalAcquirerParamsDto;
import com.tms.profilemanager.dto.modification.ModificationAcquirerDto;
import com.tms.profilemanager.dto.view.AcquirerView;
import com.tms.profilemanager.dto.view.PaymentSystemView;
import com.tms.profilemanager.dto.view.TerminalAcquirerParamsView;
import com.tms.profilemanager.dto.view.TerminalView;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {OptionalMapper.class})
public abstract class ModificationAcquirerMapper implements EntityMapper<ModificationAcquirer, ModificationAcquirerDto> {

    @Autowired
    @Lazy
    AcquirerMapper acquirerMapper;
    @Autowired
    @Lazy
    AcquirerService acquirerService;
    @Autowired
    @Lazy
    ModificationPaymentSystemMapper modificationPaymentSystemMapper;
    @Autowired
    @Lazy
    ModificationPaymentSystemService modificationPaymentSystemService;


    public void acquirerModificationMapping(TerminalView terminalView, List<ModificationAcquirer> modificationAcquirers, List<Long> groupAndParentIds) {
        Map<Long, List<TerminalAcquirerParamsView>> acquirerViews =
                terminalView.getAcquirers().stream()
                        .collect(Collectors.groupingBy(TerminalAcquirerParamsDto::getAcquirerId));
        acquirerViews.forEach((acquirerId, paramsViews) -> {
            modificationAcquirers.stream()
                    .filter(m -> acquirerId.equals(m.getAcquirerId()))
                    .forEach(modificationAcquirer -> paramsViews
                            .forEach(params -> patchModificationAcquirer(modificationAcquirer, params.getAcquirer())));

            paramsViews.forEach(params -> {
                Set<PaymentSystemView> paymentSystemViews = params.getAcquirer().getPaymentSystems();

                modificationPaymentSystemService.getPaymentSystemWithModification(groupAndParentIds, paymentSystemViews);
            });
        });
    }

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchModificationAcquirer(ModificationAcquirer modificationAcquirer, @MappingTarget AcquirerView acquirerView);

    @Mapping(target = "groupId", ignore = true)
    public abstract ModificationAcquirerDto toDto(ModificationAcquirer entity);

    @AfterMapping
    protected void afterToDto(@MappingTarget ModificationAcquirerDto.ModificationAcquirerDtoBuilder<?, ?> dto, ModificationAcquirer entity) {
        dto.groupId(entity.getGroupId());
        dto.acquirerId(entity.getAcquirerId());
    }
}

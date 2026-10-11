package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.TerminalGroupPaymentScreenLogo;
import com.tms.profilemanager.app.exception.EntityNotFoundByIdException;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.PaymentScreenLogoService;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.patch.TerminalGroupPatchDto;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class TerminalGroupProvider
        extends EntityProvider<TerminalGroup, TerminalGroupCreateDto, TerminalGroupPatchDto> {

    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired
    @Lazy
    PaymentScreenLogoService paymentScreenLogoService;

    @Override
    @Mapping(target = "paymentScreenLogos", ignore = true)
    public abstract void initializeEntity(TerminalGroupCreateDto source, @MappingTarget TerminalGroup target);

    @AfterMapping
    public void afterInitializeEntity(TerminalGroupCreateDto source, @MappingTarget TerminalGroup target) {
        if (source.getParentId() == null) {
            target.setParentPath(null);
        } else {
            TerminalGroup parentGroup = terminalGroupService.getById(source.getParentId())
                    .orElseThrow(() -> new EntityNotFoundByIdException(TerminalGroup.class, source.getParentId()));
            if (parentGroup.getParentPath() == null) {
                target.setParentPath(String.valueOf(source.getParentId()));
            } else {
                target.setParentPath(parentGroup.getParentPath() + "." + source.getParentId());
            }
        }
        if (source.getPaymentScreenLogos() != null && !source.getPaymentScreenLogos().isEmpty()) {
            IntStream.range(0, source.getPaymentScreenLogos().size())
                    .forEach(i ->
                            target.getPaymentScreenLogos().add(
                                    TerminalGroupPaymentScreenLogo.builder()
                                            .terminalGroup(target)
                                            .paymentScreenLogo(paymentScreenLogoService.findLogoById(
                                                    source.getPaymentScreenLogos().get(i))
                                            )
                                            .paymentScreenLogoOrder(i + 1)
                                            .build()
                            ));
        }
    }

    @Override
    @Mapping(target = "paymentScreenLogos", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(TerminalGroupPatchDto patchDto, @MappingTarget TerminalGroup entity);

    @AfterMapping
    protected void afterPatchEntity(TerminalGroupPatchDto source, @MappingTarget TerminalGroup target) {
        if (source.getPaymentScreenLogos() != null) {
            List<Long> paymentScreenLogoIds = source.getPaymentScreenLogos();
            Map<Long, TerminalGroupPaymentScreenLogo> existing =
                    target.getPaymentScreenLogos()
                            .stream()
                            .collect(Collectors.toMap(
                                    TerminalGroupPaymentScreenLogo::getPaymentScreenLogoId,
                                    Function.identity())
                            );

            target.getPaymentScreenLogos().removeIf(terminalGroupPaymentScreenLogo ->
                    !paymentScreenLogoIds.contains(terminalGroupPaymentScreenLogo.getPaymentScreenLogoId())
            );

            for (int i = 0; i < paymentScreenLogoIds.size(); i++) {

                Long paymentScreenLogoId = paymentScreenLogoIds.get(i);
                int order = i + 1;

                TerminalGroupPaymentScreenLogo terminalGroupPaymentScreenLogo = existing.get(paymentScreenLogoId);

                if (terminalGroupPaymentScreenLogo != null) {
                    terminalGroupPaymentScreenLogo.setPaymentScreenLogoOrder(order);
                } else {
                    target.getPaymentScreenLogos().add(
                            TerminalGroupPaymentScreenLogo.builder()
                                    .terminalGroup(target)
                                    .paymentScreenLogo(paymentScreenLogoService.findLogoById(paymentScreenLogoId))
                                    .paymentScreenLogoOrder(order)
                                    .build()
                    );
                }
            }
        }
    }

    @Override
    protected Class<TerminalGroup> getEntityClass() {
        return TerminalGroup.class;
    }
}

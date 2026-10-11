package com.tms.profilemanager.app.service.provider;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.Terminal;
import com.tms.profilemanager.app.entity.Vendor;
import com.tms.profilemanager.app.entity.VendorModel;
import com.tms.profilemanager.app.exception.DuplicateException;
import com.tms.profilemanager.app.exception.VendorModelLinkedWithTerminalException;
import com.tms.profilemanager.app.mapper.entity.util.OptionalMapper;
import com.tms.profilemanager.app.service.TerminalService;
import com.tms.profilemanager.app.service.VendorModelService;
import com.tms.profilemanager.dto.create.VendorCreateDto;
import com.tms.profilemanager.dto.create.VendorCreateDto.VendorModelInputDto;
import com.tms.profilemanager.dto.patch.VendorPatchDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public abstract class VendorProvider extends EntityProvider<Vendor, VendorCreateDto, VendorPatchDto> {

    @Autowired @Lazy TerminalService terminalService;
    @Autowired @Lazy VendorModelProvider vendorModelProvider;
    @Autowired @Lazy VendorModelService vendorModelService;

    @Override
    @Mapping(target = "models", ignore = true)
    public abstract void initializeEntity(VendorCreateDto source, @MappingTarget Vendor target);

    @AfterMapping
    protected void afterInitializeEntity(VendorCreateDto source, @MappingTarget Vendor target) {
        if (!source.getModels().isEmpty()) {
            source.getModels().stream()
                    .map(VendorModelInputDto::getName)
                    .distinct()
                    .map(modelName -> VendorModelInputDto.builder().name(modelName).build()).collect(toSet())
                    .forEach(modelInputDto -> target.getModels().add(createModelAndLinkWithVendor(modelInputDto, target)));
        }
    }

    @Override
    @Mapping(target = "models", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchEntity(VendorPatchDto patchDto, @MappingTarget Vendor entity);

    @AfterMapping
    protected void afterPatchEntity(VendorPatchDto vendorPatchDto, @MappingTarget Vendor vendor) {
        if (vendorPatchDto.getModels() != null) {
            if (vendor.getModels().isEmpty()) {
                addValidationError(VendorPatchDto.Fields.models, "validation.vendor.models.empty");
            }
            deleteModels(vendorPatchDto, vendor);
            patchVendorAndModel(vendorPatchDto, vendor);
        }
    }

    private void patchVendorAndModel(VendorPatchDto vendorPatchDto, Vendor vendor) {
        checkIfExistDuplicateNames(vendorPatchDto.getModels());
        vendor.getModels().forEach(vendorModel -> vendorPatchDto.getModels().stream()
                .filter(vendorModelInputDto -> vendorModel.getId().equals(vendorModelInputDto.getId()))
                .map(name -> VendorModelInputDto.builder().id(name.getId()).name(name.getName()).build())
                .forEach(vendorModelInputDto -> vendorModelProvider.patch(vendorModelInputDto, vendorModel))
        );
        vendorPatchDto.getModels().stream()
                .filter(vendorModelPatchDto -> vendorModelPatchDto.getId() == null)
                .forEach(vendorModelPatchDto -> {
                    VendorModelInputDto.builder().name(vendorModelPatchDto.getName()).build();
                    vendor.getModels().add(createModelAndLinkWithVendor(vendorModelPatchDto, vendor));
                });

    }

    private void deleteModels(VendorPatchDto source, Vendor target) {
        List<VendorModel> vendorModelsInVendor = vendorModelService.findVendorModelsById(target.getModels().stream()
                .map(BaseEntity::getId)
                .collect(toSet()));
        Optional<Terminal> existTerminal = terminalService.findFirstByVendorModelIdIn(vendorModelsInVendor.stream()
                .map(BaseEntity::getId)
                .collect(toSet()));
        Set<Long> existModelIds = target.getModels().stream()
                .map(VendorModel::getId)
                .collect(toSet());
        Set<Long> patchingIds = source.getModels().stream()
                .map(VendorModelInputDto::getId)
                .filter(Objects::nonNull)
                .collect(toSet());
        existModelIds.removeAll(patchingIds);
        if (existTerminal.isPresent() && existModelIds.contains(existTerminal.get().getId())) {
            var vendorModel = vendorModelService.getByIdUsingCacheWithoutAccessControl(existTerminal.get().getVendorModelId()).orElseThrow();
            throw new VendorModelLinkedWithTerminalException(vendorModel.getName(), existTerminal.get().getSerialNumber());
        }
        existModelIds.forEach(modelId -> target.getModels().removeIf(it -> it.getId().equals(modelId)));
    }

    private VendorModel createModelAndLinkWithVendor(VendorModelInputDto inputDto, Vendor vendor) {
        return VendorModel.builder().name(inputDto.getName()).vendor(vendor).build();
    }

    private void checkIfExistDuplicateNames(Set<VendorModelInputDto> modelPatchDtos) {
        long uniqueNamesCount = modelPatchDtos.stream().map(VendorModelInputDto::getName).distinct().count();
        if (uniqueNamesCount < modelPatchDtos.size()) {
            throw new DuplicateException("Model");
        }
    }

    @Override
    protected Class<Vendor> getEntityClass() {
        return Vendor.class;
    }
}

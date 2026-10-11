package com.tms.profilemanager.app.mapper.entity;

import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.entity.TerminalGroupPaymentScreenLogo;
import com.tms.profilemanager.app.service.TerminalGroupService;
import com.tms.profilemanager.dto.TerminalGroupDto;
import com.tms.profilemanager.dto.TerminalGroupShortDto;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Mapper(componentModel = "spring")
public abstract class TerminalGroupMapper implements EntityMapper<TerminalGroup, TerminalGroupDto> {

    @Autowired
    @Lazy
    TerminalGroupService terminalGroupService;
    @Autowired
    @Lazy
    PaymentScreenLogoMapper paymentScreenLogoMapper;

    @Override
    public TerminalGroupDto toDto(TerminalGroup terminalGroup) {
        if (terminalGroup == null) {
            return null;
        }
        List<TerminalGroup> givenGroupAndAllChildren = terminalGroupService.findGroupAndAllChildrenRecursively(terminalGroup.getId());
        TerminalGroupDto dto = toDtoWithChildrenRecursively(terminalGroup, givenGroupAndAllChildren);

        Set<Long> groupIds = givenGroupAndAllChildren.stream().map(BaseEntity::getId).collect(toSet());
        return addTerminalCountsToGroupRecursively(dto, terminalGroupService.getTerminalCountsForGroups(groupIds));
    }

    @Override
    public List<TerminalGroupDto> toDtoList(List<TerminalGroup> terminalGroups) {
        List<TerminalGroup> parents = terminalGroups.stream()
                .filter(it -> it.getParentPath() == null)
                .toList();

        List<TerminalGroupDto> dtoList = new ArrayList<>();
        for (TerminalGroup parent : parents) {
            dtoList.add(toDtoWithChildrenRecursively(parent, terminalGroups));
        }

        Set<Long> groupIds = dtoList.stream()
                .flatMap(dto -> getGroupIdAndAllChildrenIdsRecursively(dto).stream())
                .collect(toSet());
        return addTerminalCountsToGroupsRecursively(dtoList, terminalGroupService.getTerminalCountsForGroups(groupIds));
    }

    public abstract TerminalGroupShortDto toShortDto(TerminalGroup terminalGroup);

    private TerminalGroupDto toDtoWithChildrenRecursively(TerminalGroup parent, List<TerminalGroup> terminalGroups) {
        TerminalGroupDto parentDto = toDtoWithoutChildren(parent);
        String parentPathForChildren = Stream.of(parent.getParentPath(), String.valueOf(parent.getId()))
                .filter(Objects::nonNull)
                .collect(joining("."));
        terminalGroups.stream()
                .filter(group -> Objects.equals(parentPathForChildren, group.getParentPath()))
                .map(child -> toDtoWithChildrenRecursively(child, terminalGroups))
                .forEach(parentDto::addChild);
        return parentDto;
    }

    private TerminalGroupDto toDtoWithoutChildren(TerminalGroup entity) {
        return TerminalGroupDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .archived(entity.isArchived())
                .name(entity.getName())
                .paymentScreenLogos(
                        paymentScreenLogoMapper.toDtoList(
                                entity.getPaymentScreenLogos().stream()
                                        .sorted(Comparator.comparing(
                                                TerminalGroupPaymentScreenLogo::getPaymentScreenLogoOrder,
                                                Comparator.naturalOrder()
                                        ))
                                        .map(TerminalGroupPaymentScreenLogo::getPaymentScreenLogo)
                                        .toList()
                        )
                )
                .build();
    }

    private TerminalGroupDto addTerminalCountsToGroupRecursively(TerminalGroupDto group,
                                                                 Map<Long, Integer> groupIdTerminalCountMap) {
        if (isNotEmpty(group.getChildren())) {
            addTerminalCountsToGroupsRecursively(group.getChildren(), groupIdTerminalCountMap);
        }
        int terminalsCountInCurrentGroup = groupIdTerminalCountMap.getOrDefault(group.getId(), 0);
        int terminalsCountInAllChildren = group.getChildren() != null
                ? group.getChildren().stream().map(TerminalGroupDto::getTerminalsCount).reduce(0, Integer::sum)
                : 0;
        group.setTerminalsCount(terminalsCountInCurrentGroup + terminalsCountInAllChildren);
        return group;
    }

    private List<TerminalGroupDto> addTerminalCountsToGroupsRecursively(List<TerminalGroupDto> groups,
                                                                        Map<Long, Integer> groupIdTerminalCountMap) {
        groups.forEach(group -> addTerminalCountsToGroupRecursively(group, groupIdTerminalCountMap));
        return groups;
    }

    private Set<Long> getGroupIdAndAllChildrenIdsRecursively(TerminalGroupDto terminalGroup) {
        Set<Long> childrenIds = new HashSet<>();
        childrenIds.add(terminalGroup.getId());
        if (terminalGroup.getChildren() == null) {
            return childrenIds;
        }
        for (TerminalGroupDto child : terminalGroup.getChildren()) {
            childrenIds.addAll(getGroupIdAndAllChildrenIdsRecursively(child));
        }
        return childrenIds;
    }
}

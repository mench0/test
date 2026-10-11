package com.tms.profilemanager.app.service;

import com.tms.profilemanager.app.config.security.AuthExpressionsHolder;
import com.tms.profilemanager.app.entity.BaseEntity;
import com.tms.profilemanager.app.entity.TerminalGroup;
import com.tms.profilemanager.app.integration.humo.enums.Bank;
import com.tms.profilemanager.app.mapper.filter.TerminalGroupSearchMapper;
import com.tms.profilemanager.app.repository.TerminalAcquirerParamsRepository.TerminalAcquirerParamsSlim;
import com.tms.profilemanager.app.repository.TerminalGroupRepository;
import com.tms.profilemanager.app.repository.TerminalGroupRepository.TerminalGroupCounts;
import com.tms.profilemanager.app.service.provider.TerminalGroupProvider;
import com.tms.profilemanager.dto.create.TerminalGroupCreateDto;
import com.tms.profilemanager.dto.filter.TerminalGroupFilterDto;
import com.tms.profilemanager.dto.patch.TerminalGroupPatchDto;
import com.tms.profilemanager.dto.patch.TerminalPatchDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tms.profilemanager.dto.enums.Permission.TERMINAL_GROUP_WRITE;
import static java.util.stream.Collectors.toMap;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TerminalGroupService
        extends AbstractCrudService<TerminalGroup, TerminalGroupCreateDto, TerminalGroupPatchDto, TerminalGroupFilterDto> {

    static String GNK = "GNK";
    static String ERA = "ERA";
    static String PARENT_PATH_PATTERN = "^[0-9]*$";
    TerminalGroupRepository terminalGroupRepository;
    TerminalService terminalService;

    public TerminalGroupService(TerminalGroupProvider entityProvider,
                                TerminalGroupRepository repository,
                                TerminalGroupSearchMapper searchMapper,
                                TerminalService terminalService,
                                ApplicationEventPublisher eventPublisher) {
        super(TerminalGroup.class, entityProvider, repository, repository, searchMapper, eventPublisher);
        this.terminalGroupRepository = repository;
        this.terminalService = terminalService;
    }

    @Override
    protected boolean hasWriteAccess() {
        return AuthExpressionsHolder.hasPermission(TERMINAL_GROUP_WRITE);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        assertWriteAccess(id);
        List<TerminalGroup> groupsForDelete = findGroupAndAllChildrenRecursively(id);
        Set<TerminalAcquirerParamsSlim> terminals = terminalService.getTerminalsByGroupIds(groupsForDelete.stream().map(BaseEntity::getId).collect(Collectors.toSet()));
        terminals.forEach(terminal ->
                terminalService.patch(TerminalPatchDto.builder()
                        .id(terminal.getId())
                        .groupId(Optional.empty())
                        .build()));
        terminalGroupRepository.deleteAll(groupsForDelete);
    }

    public List<TerminalGroup> findGroupAndAllChildrenRecursively(Long pathParentId) {
        return terminalGroupRepository.findGroupAndAllChildrenRecursively(pathParentId);
    }

    public List<TerminalGroup> findByParentPath(Long id) {
        return terminalGroupRepository.findByParentPath(id);
    }

    public List<TerminalGroup> findSelfAndParents(Long id) {
        return terminalGroupRepository.findSelfAndParents(id);
    }

    public Map<Long, Integer> getTerminalCountsForGroups(Set<Long> groupIds) {
        return terminalGroupRepository.getTerminalCountsForGroups(groupIds).stream()
                .collect(toMap(TerminalGroupCounts::getGroupId, TerminalGroupCounts::getTerminalsCount));
    }

    @Transactional
    public TerminalGroup findOrCreateGroupByName(String bankId) {
        String mfo = Arrays.stream(Bank.values())
                .filter(bank -> bank.getBankId().equals(bankId))
                .map(Bank::getMfo).findFirst().get();
        List<TerminalGroup> terminalGroup = terminalGroupRepository.findParentAndChildGroups(mfo);
        TerminalGroup parent = createParentGroup(terminalGroup);
        return terminalGroup.stream()
                .filter(group -> group.getParentPath() != null && group.getParentPath().matches(PARENT_PATH_PATTERN))
                .findFirst()
                .orElseGet(() -> create(TerminalGroupCreateDto.builder().name(mfo + "_" + ERA).parentId(parent.getId()).build()));
    }

    @Transactional
    public TerminalGroup findOrCreateGNKGroupByName() {
        List<TerminalGroup> terminalGroup = terminalGroupRepository.findParentAndChildGroupsByName(GNK);
        TerminalGroup parent = createParentGroup(terminalGroup);
        return terminalGroup.stream()
                .filter(group -> group.getParentPath() != null && group.getParentPath().matches(PARENT_PATH_PATTERN))
                .findFirst()
                .orElseGet(() -> create(TerminalGroupCreateDto.builder().name(GNK).parentId(parent.getId()).build()));

    }

    @Transactional
    public TerminalGroup createParentGroup(List<TerminalGroup> terminalGroup) {
        return terminalGroup.stream()
                .filter(group -> group.getParentPath() == null)
                .findFirst()
                .orElseGet(() -> create(TerminalGroupCreateDto.builder().name(ERA).build()));
    }

}

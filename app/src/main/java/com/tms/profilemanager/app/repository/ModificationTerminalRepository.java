package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.modification.ModificationTerminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ModificationTerminalRepository extends JpaRepository<ModificationTerminal, Long>, QuerydslPredicateExecutor<ModificationTerminal> {

    List<ModificationTerminal> findAllByGroupIdInOrderByGroupId(List<Long> groupIds);

}

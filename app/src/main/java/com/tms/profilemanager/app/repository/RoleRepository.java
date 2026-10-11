package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {

    Role findByName(String name);

    @Query("select r from Role r where r.terminalDefaultRole = true")
    Optional<Role> findTerminalDefaultRole();
}

package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    User findByUsername(String username);

    @Query("select u.id from User u where u.role.id = :roleId")
    Set<Long> findByRoleId(@Param("roleId") Long roleId);
}

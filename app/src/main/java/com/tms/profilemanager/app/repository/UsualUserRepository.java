package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.UsualUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UsualUserRepository extends JpaRepository<UsualUser, Long>, QuerydslPredicateExecutor<UsualUser> {

    Optional<UsualUser> findByEmailAndArchived(String email, Boolean isArchived);
}
package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.dictionary.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CardTypeRepository extends JpaRepository<CardType, Long>, QuerydslPredicateExecutor<CardType> {
}

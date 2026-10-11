package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.dictionary.CardBinRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CardBinRangeRepository extends JpaRepository<CardBinRange, Long>, QuerydslPredicateExecutor<CardBinRange> {
}

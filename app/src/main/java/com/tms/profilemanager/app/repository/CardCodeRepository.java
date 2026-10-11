package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.dictionary.CardCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardCodeRepository extends JpaRepository<CardCode, Long>, QuerydslPredicateExecutor<CardCode> {

    @Query(value = """
                SELECT DISTINCT ct.name
                FROM card_code cc
                JOIN card_type ct ON cc.card_type_id = ct.id
                ORDER BY ct.name
            """,
            nativeQuery = true
    )
    List<String> findDistinctCardTypeNames();

    @Query(value = """
        SELECT
            ct.id AS id,
            cc.code AS code,
            ct.name AS name
        FROM card_code cc
        LEFT JOIN card_type ct
            ON cc.card_type_id = ct.id
        WHERE ct.name IN (:names)
        ORDER BY ct.created_at DESC;
        """,
            nativeQuery = true)
    List<CardCodeSlim> findByCardTypeNamesOrdered(@Param("names") List<String> names);

    interface CardCodeSlim {
        Long getId();
        String getCode();
        String getName();
    }
}

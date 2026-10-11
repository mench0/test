package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.CardRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CardRangeRepository extends
        JpaRepository<CardRange, Long>, QuerydslPredicateExecutor<CardRange> {

    @Query(value = """
            select * from card_range cr
                left outer join card_range_limit_mapping crlm on cr.id = crlm.card_range_id
                left outer join general_limit gl on gl.id = crlm.limit_id
                left outer join contact_limit cl on gl.contact_limit_id = cl.id
                left outer join contactless_limit cll on cll.id = gl.contactless_limit_id
                left outer join drl d on cll.id = d.contactless_limit_id
            where cr.acquirer_id = ?1
            """, nativeQuery = true)
    List<CardRange> getByAcquirerId(Long acquirerId);

}

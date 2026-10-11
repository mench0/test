package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.TerminalGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TerminalGroupRepository
        extends JpaRepository<TerminalGroup, Long>, QuerydslPredicateExecutor<TerminalGroup> {

    @Query(value = "FROM TerminalGroup WHERE name in ('ERA', :name) ORDER BY parentPath DESC")
    List<TerminalGroup> findParentAndChildGroupsByName(@Param("name") String name);

    @Query(value = "FROM TerminalGroup WHERE name = 'ERA' OR name LIKE concat(:name, '_ERA') ORDER BY parentPath DESC")
    List<TerminalGroup> findParentAndChildGroups(@Param("name") String name);

    @Query(value = "FROM TerminalGroup t WHERE t.id = :id or t.parentPath like concat(:id, '')" +
            "or t.parentPath like concat(:id, '.%') " +
            "or t.parentPath like concat('%.', :id, '.%') " +
            "or t.parentPath like concat('%.', :id)")
    List<TerminalGroup> findGroupAndAllChildrenRecursively(@Param("id") Long id);

    @Query(value = """
            SELECT * FROM terminal_group t WHERE t.id = :id
            OR t.parent_path = cast(:id as VARCHAR)
            OR t.parent_path LIKE concat(cast(:id as VARCHAR), '.%')
            OR t.parent_path LIKE concat('%.', :id)
            """, nativeQuery = true)
    List<TerminalGroup> findByParentPath(@Param("id") Long id);

    @Query(value = """
        SELECT * FROM terminal_group t
        WHERE t.id = :id
        OR t.id = ANY(
            CAST(
                string_to_array(
                    (SELECT parent_path FROM terminal_group WHERE id = :id), '.'
                ) AS bigint[]
            )
        )
        """, nativeQuery = true)
    List<TerminalGroup> findSelfAndParents(@Param("id") Long id);

    @Query(value = """
            select t.group_id as groupId, count(*) as terminalsCount
            from terminal t
            where t.group_id in :groupIds
            group by t.group_id
            """, nativeQuery = true)
    List<TerminalGroupCounts> getTerminalCountsForGroups(@Param("groupIds") Set<Long> groupIds);

    interface TerminalGroupCounts {
        Long getGroupId();
        Integer getTerminalsCount();
    }
}

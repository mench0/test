package com.tms.profilemanager.app.repository;

import com.tms.profilemanager.app.entity.TerminalAcquirerOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TerminalAcquirerOperationRepository extends JpaRepository<TerminalAcquirerOperation, Long> {

    @Modifying
    @Query(value = """
            DELETE FROM terminal_acquirer_params_operations_mapping tao
            WHERE EXISTS (
                SELECT 1 FROM terminal_acquirer_params tap
                WHERE tao.terminal_acquirer_params_id = tap.id
                AND tap.acquirer_id = :acquirerId
            )
            AND tao.operation = :operation
            """, nativeQuery = true)
    void deleteByAcquirerIdAndOperation(@Param("acquirerId") Long acquirerId, @Param("operation") String operation);

    @Modifying
    @Query(value = """
            INSERT INTO terminal_acquirer_params_operations_mapping(id, terminal_acquirer_params_id, operation, created_at, created_by, last_updated_at, last_updated_by)
            SELECT NEXTVAL('terminal_acquirer_operation_seq'),tap.id, :operation,now(),:createdUserId,now(),:createdUserId
            FROM terminal_acquirer_params tap
            WHERE tap.acquirer_id = :acquirerId
            """, nativeQuery = true)
    void insertTerminalAcquirerOperations(@Param("acquirerId") Long acquirerId, @Param("operation") String operation, @Param("createdUserId") Long createdUserId);
}

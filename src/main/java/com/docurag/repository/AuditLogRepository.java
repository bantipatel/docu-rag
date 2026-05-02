package com.docurag.repository;
import com.docurag.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserId(UUID userId);
    List<AuditLog> findByStatus(String status);
    List<AuditLog> findByOperationType(String operationType);
    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:userId IS NULL OR a.user.id = :userId) AND " +
           "(:operationType IS NULL OR a.operationType = :operationType) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate)")
    Page<AuditLog> findWithFilters(@Param("status") String status,
                                   @Param("userId") UUID userId,
                                   @Param("operationType") String operationType,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);
}

package com.docurag.service;

import com.docurag.dto.response.AuditLogResponse;
import com.docurag.entity.AuditLog;
import com.docurag.entity.User;
import com.docurag.mapper.AuditLogMapper;
import com.docurag.repository.AuditLogRepository;
import com.docurag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    public void log(String operationType, String status, String requestPayload,
                    Integer responseStatus, String errorMessage, UUID userId) {
        try {
            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
            AuditLog auditLog = AuditLog.builder()
                    .operationType(operationType)
                    .status(status)
                    .requestPayload(requestPayload)
                    .responseStatus(responseStatus)
                    .errorMessage(errorMessage)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to write audit log", e);
        }
    }

    public Page<AuditLogResponse> getLogs(String status, UUID userId, String operationType,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           Pageable pageable) {
        return auditLogRepository.findWithFilters(status, userId, operationType, startDate, endDate, pageable)
                .map(auditLogMapper::toResponse);
    }
}

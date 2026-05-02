package com.docurag.dto.response;
import java.time.LocalDateTime;
import java.util.UUID;
public record AuditLogResponse(
    UUID id,
    String operationType,
    String status,
    String requestPayload,
    Integer responseStatus,
    String errorMessage,
    UUID userId,
    LocalDateTime createdAt
) {}

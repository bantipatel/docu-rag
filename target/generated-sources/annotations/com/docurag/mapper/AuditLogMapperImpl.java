package com.docurag.mapper;

import com.docurag.dto.response.AuditLogResponse;
import com.docurag.entity.AuditLog;
import com.docurag.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-02T19:33:46+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogResponse toResponse(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        String operationType = null;
        String status = null;
        String requestPayload = null;
        Integer responseStatus = null;
        String errorMessage = null;
        LocalDateTime createdAt = null;

        userId = auditLogUserId( auditLog );
        id = auditLog.getId();
        operationType = auditLog.getOperationType();
        status = auditLog.getStatus();
        requestPayload = auditLog.getRequestPayload();
        responseStatus = auditLog.getResponseStatus();
        errorMessage = auditLog.getErrorMessage();
        createdAt = auditLog.getCreatedAt();

        AuditLogResponse auditLogResponse = new AuditLogResponse( id, operationType, status, requestPayload, responseStatus, errorMessage, userId, createdAt );

        return auditLogResponse;
    }

    private UUID auditLogUserId(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }
        User user = auditLog.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

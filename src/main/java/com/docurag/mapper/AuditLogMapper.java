package com.docurag.mapper;
import com.docurag.dto.response.AuditLogResponse;
import com.docurag.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(source = "user.id", target = "userId")
    AuditLogResponse toResponse(AuditLog auditLog);
}

package com.docurag.dto.request;
import jakarta.validation.constraints.NotBlank;
public record SearchRequest(
    @NotBlank String queryText,
    String documentType,
    String sourceUrl
) {}

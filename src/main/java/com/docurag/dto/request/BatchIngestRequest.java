package com.docurag.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
public record BatchIngestRequest(@NotEmpty List<@Valid DocumentIngestItem> documents) {
    public record DocumentIngestItem(
        @jakarta.validation.constraints.NotBlank String content,
        @jakarta.validation.constraints.NotBlank String filename,
        @jakarta.validation.constraints.NotBlank String documentType,
        String sourceUrl,
        String metadataTags,
        String uploadedByUsername
    ) {}
}

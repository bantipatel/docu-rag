package com.docurag.dto.response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public record BatchIngestResponse(
    UUID batchId,
    int documentCount,
    int totalChunks,
    LocalDateTime processedAt,
    List<DocumentIngestResult> results
) {
    public record DocumentIngestResult(
        String filename,
        UUID documentId,
        int chunkCount,
        String status,
        String error
    ) {}
}

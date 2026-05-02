package com.docurag.dto.response;
import java.util.UUID;
public record SearchResultItem(
    UUID chunkId,
    String chunkText,
    float vectorScore,
    float bm25Score,
    int combinedRank,
    float confidenceScore,
    UUID documentId,
    String filename,
    int sequenceNumber
) {}

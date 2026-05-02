package com.docurag.dto.response;
import java.util.List;
import java.util.UUID;
public record SearchResponse(
    UUID queryId,
    String queryText,
    List<SearchResultItem> results,
    boolean lowConfidence
) {}

package com.docurag.controller;

import com.docurag.dto.request.BatchIngestRequest;
import com.docurag.dto.response.BatchIngestResponse;
import com.docurag.service.IngestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
@Tag(name = "Documents", description = "Document ingestion endpoints")
public class DocumentController {

    private final IngestService ingestService;

    @PostMapping("/ingest-batch")
    @Operation(summary = "Ingest a batch of documents")
    public ResponseEntity<BatchIngestResponse> ingestBatch(@Valid @RequestBody BatchIngestRequest request) {
        return ResponseEntity.ok(ingestService.ingestBatch(request));
    }
}

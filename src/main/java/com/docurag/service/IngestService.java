package com.docurag.service;

import com.docurag.dto.request.BatchIngestRequest;
import com.docurag.dto.response.BatchIngestResponse;
import com.docurag.entity.Chunk;
import com.docurag.entity.Document;
import com.docurag.entity.User;
import com.docurag.repository.ChunkRepository;
import com.docurag.repository.DocumentRepository;
import com.docurag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;
    private final AuditLogService auditLogService;

    @Value("${ingest.chunk-size:500}")
    private int chunkSize;

    @Value("${ingest.chunk-overlap:50}")
    private int chunkOverlap;

    public BatchIngestResponse ingestBatch(BatchIngestRequest request) {
        UUID batchId = UUID.randomUUID();
        List<BatchIngestResponse.DocumentIngestResult> results = new ArrayList<>();
        int totalChunks = 0;

        for (BatchIngestRequest.DocumentIngestItem item : request.documents()) {
            BatchIngestResponse.DocumentIngestResult result = ingestDocument(item);
            results.add(result);
            if ("completed".equals(result.status())) totalChunks += result.chunkCount();
        }

        auditLogService.log("ingest", "success",
                "{\"batchId\":\"" + batchId + "\",\"documentCount\":" + request.documents().size() + "}",
                200, null, null);

        return new BatchIngestResponse(batchId, request.documents().size(), totalChunks, LocalDateTime.now(), results);
    }

    private BatchIngestResponse.DocumentIngestResult ingestDocument(BatchIngestRequest.DocumentIngestItem item) {
        Document doc = null;
        try {
            User uploader = item.uploadedByUsername() != null
                    ? userRepository.findByUsername(item.uploadedByUsername()).orElse(null)
                    : null;

            doc = Document.builder()
                    .filename(item.filename())
                    .documentType(item.documentType())
                    .sourceUrl(item.sourceUrl())
                    .uploadedBy(uploader)
                    .uploadTimestamp(LocalDateTime.now())
                    .ingestionStatus("pending")
                    .totalChunks(0)
                    .metadataTags(item.metadataTags())
                    .build();
            doc = documentRepository.save(doc);

            List<String> chunks = splitIntoChunks(item.content(), chunkSize, chunkOverlap);
            List<Chunk> savedChunks = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                String chunkText = chunks.get(i);
                float[] embedding = embeddingService.embed(chunkText);
                Chunk chunk = Chunk.builder()
                        .document(doc)
                        .content(chunkText)
                        .characterCount(chunkText.length())
                        .sequenceNumber(i)
                        .createdAt(LocalDateTime.now())
                        .build();
                chunk.setEmbeddingFromFloatArray(embedding);
                savedChunks.add(chunkRepository.save(chunk));
            }

            doc.setIngestionStatus("completed");
            doc.setTotalChunks(savedChunks.size());
            documentRepository.save(doc);

            log.info("Ingested document {} with {} chunks", item.filename(), savedChunks.size());
            return new BatchIngestResponse.DocumentIngestResult(
                    item.filename(), doc.getId(), savedChunks.size(), "completed", null);

        } catch (Exception e) {
            log.error("Failed to ingest document {}", item.filename(), e);
            if (doc != null) {
                doc.setIngestionStatus("failed");
                documentRepository.save(doc);
            }
            auditLogService.log("ingest", "failed",
                    "{\"filename\":\"" + item.filename() + "\"}", 500, e.getMessage(), null);
            return new BatchIngestResponse.DocumentIngestResult(
                    item.filename(), doc != null ? doc.getId() : null, 0, "failed", e.getMessage());
        }
    }

    private List<String> splitIntoChunks(String text, int size, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) return chunks;
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + size, text.length());
            chunks.add(text.substring(start, end));
            start += (size - overlap);
            if (start >= text.length()) break;
        }
        return chunks;
    }
}

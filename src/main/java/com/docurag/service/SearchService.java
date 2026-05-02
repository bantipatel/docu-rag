package com.docurag.service;

import com.docurag.dto.request.SearchRequest;
import com.docurag.dto.response.SearchResponse;
import com.docurag.dto.response.SearchResultItem;
import com.docurag.entity.Chunk;
import com.docurag.entity.SearchResult;
import com.docurag.repository.ChunkRepository;
import com.docurag.repository.SearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ChunkRepository chunkRepository;
    private final SearchResultRepository searchResultRepository;
    private final EmbeddingService embeddingService;
    private final AuditLogService auditLogService;

    @Value("${search.vector-weight:0.6}")
    private float vectorWeight;

    @Value("${search.bm25-weight:0.4}")
    private float bm25Weight;

    @Value("${search.confidence-threshold:0.3}")
    private float confidenceThreshold;

    public SearchResponse search(SearchRequest request) {
        UUID queryId = UUID.randomUUID();
        try {
            float[] queryEmbedding = embeddingService.embed(request.queryText());
            List<Chunk> allChunks = chunkRepository.findAll();

            // Filter by documentType or sourceUrl if provided
            if (request.documentType() != null || request.sourceUrl() != null) {
                allChunks = allChunks.stream().filter(c -> {
                    boolean match = true;
                    if (request.documentType() != null)
                        match = request.documentType().equals(c.getDocument().getDocumentType());
                    if (request.sourceUrl() != null)
                        match = match && request.sourceUrl().equals(c.getDocument().getSourceUrl());
                    return match;
                }).collect(Collectors.toList());
            }

            // Compute BM25 scores
            Map<UUID, Float> bm25Scores = computeBm25(request.queryText(), allChunks);

            // Score and rank
            record ScoredChunk(Chunk chunk, float vectorScore, float bm25Score, float combined) {}
            List<ScoredChunk> scored = new ArrayList<>();
            for (Chunk chunk : allChunks) {
                float[] chunkEmbedding = chunk.getEmbeddingAsFloatArray();
                float vScore = chunkEmbedding.length > 0 ? embeddingService.cosineSimilarity(queryEmbedding, chunkEmbedding) : 0f;
                float bScore = bm25Scores.getOrDefault(chunk.getId(), 0f);
                float combined = vectorWeight * vScore + bm25Weight * bScore;
                scored.add(new ScoredChunk(chunk, vScore, bScore, combined));
            }
            scored.sort((a, b) -> Float.compare(b.combined(), a.combined()));

            // Take top 10
            List<ScoredChunk> top = scored.stream().limit(10).toList();
            boolean lowConfidence = top.isEmpty() || top.get(0).combined() < confidenceThreshold;

            List<SearchResultItem> items = new ArrayList<>();
            for (int i = 0; i < top.size(); i++) {
                ScoredChunk sc = top.get(i);
                float confidence = Math.min(1.0f, sc.combined() / Math.max(confidenceThreshold, 0.01f));
                items.add(new SearchResultItem(
                        sc.chunk().getId(),
                        sc.chunk().getContent(),
                        sc.vectorScore(),
                        sc.bm25Score(),
                        i + 1,
                        Math.min(1.0f, confidence),
                        sc.chunk().getDocument().getId(),
                        sc.chunk().getDocument().getFilename(),
                        sc.chunk().getSequenceNumber()
                ));

                // Persist search result
                try {
                    SearchResult sr = SearchResult.builder()
                            .queryId(queryId)
                            .chunk(sc.chunk())
                            .vectorSimilarityScore(sc.vectorScore())
                            .bm25Score(sc.bm25Score())
                            .combinedRank(i + 1)
                            .confidenceScore(Math.min(1.0f, confidence))
                            .createdAt(LocalDateTime.now())
                            .build();
                    searchResultRepository.save(sr);
                } catch (Exception e) {
                    log.warn("Failed to persist search result", e);
                }
            }

            auditLogService.log("search", "success",
                    "{\"queryId\":\"" + queryId + "\",\"query\":\"" + request.queryText().replace("\"", "'") + "\"}",
                    200, null, null);

            return new SearchResponse(queryId, request.queryText(), items, lowConfidence);

        } catch (Exception e) {
            log.error("Search failed", e);
            auditLogService.log("search", "failed",
                    "{\"query\":\"" + request.queryText().replace("\"", "'") + "\"}", 500, e.getMessage(), null);
            throw e;
        }
    }

    private Map<UUID, Float> computeBm25(String query, List<Chunk> chunks) {
        String[] queryTerms = query.toLowerCase().split("\\s+");
        double k1 = 1.5, b = 0.75;
        double avgLen = chunks.stream().mapToInt(c -> c.getContent().split("\\s+").length).average().orElse(1);

        Map<String, Long> docFreq = new HashMap<>();
        for (Chunk chunk : chunks) {
            Set<String> seen = new HashSet<>(Arrays.asList(chunk.getContent().toLowerCase().split("\\s+")));
            for (String t : seen) docFreq.merge(t, 1L, Long::sum);
        }

        Map<UUID, Float> scores = new HashMap<>();
        int N = chunks.size();
        for (Chunk chunk : chunks) {
            String[] words = chunk.getContent().toLowerCase().split("\\s+");
            Map<String, Long> tf = new HashMap<>();
            for (String w : words) tf.merge(w, 1L, Long::sum);
            double score = 0;
            for (String term : queryTerms) {
                long df = docFreq.getOrDefault(term, 0L);
                if (df == 0) continue;
                double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1);
                double termFreq = tf.getOrDefault(term, 0L);
                double tfNorm = (termFreq * (k1 + 1)) / (termFreq + k1 * (1 - b + b * words.length / avgLen));
                score += idf * tfNorm;
            }
            scores.put(chunk.getId(), (float) score);
        }

        // Normalize BM25 scores to [0,1]
        float max = scores.values().stream().max(Float::compare).orElse(1f);
        if (max > 0) scores.replaceAll((k, v) -> v / max);
        return scores;
    }
}

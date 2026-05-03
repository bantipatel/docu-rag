package com.docurag.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // References a query session; stored as plain UUID until QuerySession entity is defined
    @Column(name = "query_id", nullable = false)
    private UUID queryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chunk_id", nullable = false)
    private Chunk chunk;

    @Column(name = "vector_similarity_score")
    private Float vectorSimilarityScore;

    @Column(name = "bm25_score")
    private Float bm25Score;

    @Column(name = "combined_rank")
    private Integer combinedRank;

    @Column(name = "confidence_score")
    private Float confidenceScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

package com.docurag.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "source_url")
    private String sourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "upload_timestamp", nullable = false)
    private LocalDateTime uploadTimestamp;

    @Column(name = "ingestion_status", nullable = false)
    private String ingestionStatus;

    @Column(name = "total_chunks")
    private Integer totalChunks;

    @Column(name = "metadata_tags", columnDefinition = "TEXT")
    private String metadataTags;
}

package com.docurag.repository;
import com.docurag.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    List<Chunk> findByDocumentId(UUID documentId);
    List<Chunk> findByDocumentIdOrderBySequenceNumber(UUID documentId);
}

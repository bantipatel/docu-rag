package com.docurag.repository;
import com.docurag.entity.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SearchResultRepository extends JpaRepository<SearchResult, UUID> {
    List<SearchResult> findByQueryId(UUID queryId);
    List<SearchResult> findByChunkId(UUID chunkId);
}

package com.docurag.repository;
import com.docurag.entity.Document;
import com.docurag.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByUploadedBy(User user);
    List<Document> findByIngestionStatus(String ingestionStatus);
}

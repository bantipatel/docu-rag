# docu-rag

Internal knowledge search system using Retrieval-Augmented Generation (RAG).

## Tech Stack
- Java 21 + Spring Boot 3.3.0
- H2 in-memory database
- LangChain4j for document chunking
- Local word-hash embeddings with cosine similarity
- BM25 keyword scoring
- MapStruct + Lombok
- SpringDoc OpenAPI (Swagger UI)

## How to Run

```bash
mvn spring-boot:run
```

On startup, 3 demo documents are automatically ingested (Spring Boot 3.x, Kubernetes, RAG architecture).

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/documents/ingest-batch | Ingest documents |
| POST | /api/v1/search | Semantic + BM25 search |
| GET  | /api/v1/chunks/{chunkId} | Get chunk by ID |
| GET  | /api/v1/audit/logs | Paginated audit logs |

## Swagger UI
http://localhost:8080/swagger-ui.html

## H2 Console
http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:ragdb`  
Username: `sa` / Password: *(empty)*

## Search Example

```json
POST /api/v1/search
{
  "queryText": "How does Spring Boot 3 handle auto-configuration?"
}
```

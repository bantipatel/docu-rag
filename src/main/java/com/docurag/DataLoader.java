package com.docurag;

import com.docurag.dto.request.BatchIngestRequest;
import com.docurag.entity.User;
import com.docurag.repository.UserRepository;
import com.docurag.service.IngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final IngestService ingestService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Loading demo data...");

        User admin = User.builder()
                .username("admin")
                .email("admin@docurag.com")
                .role("admin")
                .createdAt(LocalDateTime.now())
                .build();
        User engineer = User.builder()
                .username("engineer1")
                .email("engineer1@docurag.com")
                .role("user")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.saveAll(List.of(admin, engineer));

        String springBootDoc = """
                Spring Boot 3.x Auto-configuration
                Spring Boot 3 introduces significant improvements in auto-configuration. The new auto-configuration system uses
                @AutoConfiguration instead of the legacy spring.factories mechanism. Developers can now define auto-configurations
                in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports.

                Spring Boot 3 requires Java 17 as a minimum and supports Java 21 with virtual threads. The spring.threads.virtual.enabled
                property enables Project Loom virtual threads across the entire application.

                Actuator in Spring Boot 3 exposes health, metrics, and tracing endpoints. The /actuator/health endpoint returns
                application health status. Micrometer Tracing replaces Spring Cloud Sleuth for distributed tracing.

                Spring Data JPA 3 with Hibernate 6 brings significant performance improvements. The new @QueryHints annotation
                and improved query optimisation reduce N+1 query problems. Jakarta Persistence 3.1 replaces javax.persistence.

                Spring Security 6 in Spring Boot 3 requires lambda DSL configuration. The legacy WebSecurityConfigurerAdapter
                is removed. SecurityFilterChain beans replace it for HTTP security configuration.
                """;

        String kubernetesDoc = """
                Kubernetes Pod Scheduling and Resource Management
                Kubernetes schedules Pods onto Nodes based on resource requests and limits. Each container in a Pod can specify
                CPU and memory requests and limits. The scheduler finds a Node that satisfies all constraints including resource
                requests, node selectors, affinity, and taints/tolerations.

                Resource Quotas limit total resource consumption in a Namespace. LimitRange objects set default requests and limits
                for containers. Horizontal Pod Autoscaler scales deployments based on CPU utilization or custom metrics.

                Kubernetes Services expose Pods via a stable IP address and DNS name. ClusterIP services are internal-only.
                NodePort exposes services on each Node IP at a static port. LoadBalancer integrates with cloud provider load balancers.

                ConfigMaps store non-confidential configuration data as key-value pairs. Secrets store sensitive data such as passwords
                and API tokens. Both can be mounted as volumes or exposed as environment variables inside containers.

                Kubernetes Ingress manages external HTTP/HTTPS access to services. Ingress controllers implement the Ingress resource.
                Popular options include nginx-ingress, Traefik, and cloud-native controllers like AWS ALB Ingress Controller.
                """;

        String ragDoc = """
                Retrieval-Augmented Generation (RAG) Architecture
                RAG combines information retrieval with language model generation. Documents are ingested, chunked, and embedded
                into a vector store. At query time, the query is embedded and the nearest chunks are retrieved. The language model
                generates an answer using the retrieved context.

                Document chunking strategies significantly affect retrieval quality. Fixed-size character chunking with overlap
                (e.g. 500 chars, 50 char overlap) ensures continuity across chunk boundaries. Semantic chunking splits on
                paragraph or sentence boundaries to preserve meaning.

                Vector similarity search uses cosine similarity or dot product to find semantically similar chunks. BM25 keyword
                search complements vector search by catching exact keyword matches. Hybrid search combines both for better recall.

                Confidence scoring helps identify low-quality retrievals. If the top combined score falls below a threshold,
                the system can indicate low confidence and avoid hallucinated answers.

                Embedding models convert text to dense float vectors. Sentence-transformers and OpenAI text-embedding models
                are popular choices. Embedding dimension typically ranges from 384 (all-MiniLM-L6-v2) to 1536 (OpenAI ada-002).
                """;

        BatchIngestRequest request = new BatchIngestRequest(List.of(
                new BatchIngestRequest.DocumentIngestItem(springBootDoc, "spring-boot-3-guide.md", "markdown", null, "{\"topic\":\"spring-boot\"}", "admin"),
                new BatchIngestRequest.DocumentIngestItem(kubernetesDoc, "kubernetes-scheduling.md", "markdown", null, "{\"topic\":\"kubernetes\"}", "admin"),
                new BatchIngestRequest.DocumentIngestItem(ragDoc, "rag-architecture.md", "markdown", null, "{\"topic\":\"rag\"}", "admin")
        ));

        ingestService.ingestBatch(request);
        log.info("Demo data loaded. Swagger UI available at http://localhost:8080/swagger-ui.html");
    }
}

package com.docurag.controller;

import com.docurag.entity.Chunk;
import com.docurag.exception.ResourceNotFoundException;
import com.docurag.repository.ChunkRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chunks")
@RequiredArgsConstructor
@Tag(name = "Chunks", description = "Chunk retrieval endpoints")
public class ChunkController {

    private final ChunkRepository chunkRepository;

    @GetMapping("/{chunkId}")
    @Operation(summary = "Get chunk details by ID")
    public ResponseEntity<Chunk> getChunk(@PathVariable UUID chunkId) {
        Chunk chunk = chunkRepository.findById(chunkId)
                .orElseThrow(() -> new ResourceNotFoundException("Chunk not found: " + chunkId));
        return ResponseEntity.ok(chunk);
    }
}

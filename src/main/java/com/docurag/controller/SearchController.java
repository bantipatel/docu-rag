package com.docurag.controller;

import com.docurag.dto.request.SearchRequest;
import com.docurag.dto.response.SearchResponse;
import com.docurag.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Validated
@Tag(name = "Search", description = "Semantic search endpoints")
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    @Operation(summary = "Search across ingested documents")
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchService.search(request));
    }
}

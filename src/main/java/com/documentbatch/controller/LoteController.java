package com.documentbatch.controller;

import com.documentbatch.dto.*;
import com.documentbatch.enums.StatusLote;
import com.documentbatch.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @PostMapping
    public ResponseEntity<LoteResponse> criarLote(@Valid @RequestBody CriarLoteRequest request) {
        LoteResponse response = loteService.criarLote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<LoteResponse>> listarLotes(
            @RequestParam(required = false) StatusLote status,
            @RequestParam(required = false) String operador,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        PagedResponse<LoteResponse> response = loteService.listarLotes(status, operador, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoteResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request) {

        LoteResponse response = loteService.atualizarStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}

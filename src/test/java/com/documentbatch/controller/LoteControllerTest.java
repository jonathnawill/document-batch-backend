package com.documentbatch.controller;

import com.documentbatch.dto.*;
import com.documentbatch.enums.StatusLote;
import com.documentbatch.exception.GlobalExceptionHandler;
import com.documentbatch.exception.LoteNaoEncontradoException;
import com.documentbatch.exception.TransicaoStatusInvalidaException;
import com.documentbatch.service.LoteService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoteService loteService;

    @InjectMocks
    private LoteController loteController;

    private LoteResponse loteResponse;

    @BeforeEach
    void setUp() {
        var validator = new SpringValidatorAdapter(
                Validation.buildDefaultValidatorFactory().getValidator()
        );

        mockMvc = MockMvcBuilders.standaloneSetup(loteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        DocumentoResponse docResponse = new DocumentoResponse();
        docResponse.setId(1L);
        docResponse.setTipo("RG");
        docResponse.setNome("rg_frente.jpg");

        loteResponse = new LoteResponse();
        loteResponse.setId(1L);
        loteResponse.setOperador("joao.silva");
        loteResponse.setProcesso("ABERTURA_CONTA");
        loteResponse.setStatus(StatusLote.PENDENTE);
        loteResponse.setDataCriacao(null);
        loteResponse.setDocumentos(List.of(docResponse));
    }

    // --- POST /api/lotes ---
    @Test
    void criarLote_comDadosValidos_deveRetornar201() throws Exception {
        when(loteService.criarLote(any())).thenReturn(loteResponse);

        mockMvc.perform(post("/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "operador": "joao.silva",
                                  "processo": "ABERTURA_CONTA",
                                  "documentos": [{ "tipo": "RG", "nome": "rg_frente.jpg" }]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.operador").value("joao.silva"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void criarLote_semOperador_deveRetornar400() throws Exception {
        mockMvc.perform(post("/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "processo": "ABERTURA_CONTA",
                                  "documentos": [{ "tipo": "RG", "nome": "rg_frente.jpg" }]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void criarLote_semDocumentos_deveRetornar400() throws Exception {
        mockMvc.perform(post("/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "operador": "joao.silva",
                                  "processo": "ABERTURA_CONTA",
                                  "documentos": []
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/lotes ---
    @Test
    void listarLotes_semFiltros_deveRetornar200() throws Exception {
        PagedResponse<LoteResponse> pagina = PagedResponse.of(new PageImpl<>(List.of(loteResponse)));
        when(loteService.listarLotes(isNull(), isNull(), any())).thenReturn(pagina);

        mockMvc.perform(get("/api/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].operador").value("joao.silva"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void listarLotes_comStatusPendente_deveRetornar200() throws Exception {
        PagedResponse<LoteResponse> pagina = PagedResponse.of(new PageImpl<>(List.of(loteResponse)));
        when(loteService.listarLotes(eq(StatusLote.PENDENTE), isNull(), any())).thenReturn(pagina);

        mockMvc.perform(get("/api/lotes").param("status", "PENDENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDENTE"));
    }

    @Test
    void listarLotes_comOperador_deveRetornar200() throws Exception {
        PagedResponse<LoteResponse> pagina = PagedResponse.of(new PageImpl<>(List.of(loteResponse)));
        when(loteService.listarLotes(isNull(), eq("joao"), any())).thenReturn(pagina);

        mockMvc.perform(get("/api/lotes").param("operador", "joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].operador").value("joao.silva"));
    }

    // --- PATCH /api/lotes/{id}/status ---

    @Test
    void atualizarStatus_comDadosValidos_deveRetornar200() throws Exception {
        loteResponse.setStatus(StatusLote.EXPORTADO);
        when(loteService.atualizarStatus(eq(1L), eq(StatusLote.EXPORTADO))).thenReturn(loteResponse);

        mockMvc.perform(patch("/api/lotes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"EXPORTADO\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPORTADO"));
    }

    @Test
    void atualizarStatus_loteInexistente_deveRetornar404() throws Exception {
        when(loteService.atualizarStatus(eq(99L), any()))
                .thenThrow(new LoteNaoEncontradoException(99L));

        mockMvc.perform(patch("/api/lotes/99/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"EXPORTADO\" }"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void atualizarStatus_loteExportado_deveRetornar422() throws Exception {
        when(loteService.atualizarStatus(eq(1L), any()))
                .thenThrow(new TransicaoStatusInvalidaException("Lote com status EXPORTADO não pode ser alterado"));

        mockMvc.perform(patch("/api/lotes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"REJEITADO\" }"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Lote com status EXPORTADO não pode ser alterado"));
    }

    @Test
    void atualizarStatus_semStatus_deveRetornar400() throws Exception {
        mockMvc.perform(patch("/api/lotes/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

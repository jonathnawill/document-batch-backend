package com.documentbatch.service;

import com.documentbatch.dto.CriarDocumentoRequest;
import com.documentbatch.dto.CriarLoteRequest;
import com.documentbatch.dto.LoteResponse;
import com.documentbatch.dto.PagedResponse;
import com.documentbatch.entity.DateEntity;
import com.documentbatch.entity.Documento;
import com.documentbatch.entity.Lote;
import com.documentbatch.enums.StatusLote;
import com.documentbatch.exception.LoteNaoEncontradoException;
import com.documentbatch.exception.TransicaoStatusInvalidaException;
import com.documentbatch.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private LoteService loteService;

    private Lote loteExemplo;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        Documento doc = new Documento();
        doc.setId(1L);
        doc.setTipo("RG");
        doc.setNome("rg_frente.jpg");

        loteExemplo = new Lote();
        loteExemplo.setId(1L);
        loteExemplo.setOperador("joao.silva");
        loteExemplo.setProcesso("ABERTURA_CONTA");
        loteExemplo.setStatus(StatusLote.PENDENTE);
        loteExemplo.setDateEntity(new DateEntity(LocalDateTime.now(), LocalDateTime.now(), true));
        loteExemplo.getDocumentos().add(doc);
        doc.setLote(loteExemplo);
    }

    // --- criarLote ---

    @Test
    void criarLote_deveRetornarLoteResponse() {
        CriarDocumentoRequest docRequest = new CriarDocumentoRequest();
        docRequest.setTipo("RG");
        docRequest.setNome("rg_frente.jpg");

        CriarLoteRequest request = new CriarLoteRequest();
        request.setOperador("joao.silva");
        request.setProcesso("ABERTURA_CONTA");
        request.setDocumentos(List.of(docRequest));

        when(loteRepository.save(any(Lote.class))).thenReturn(loteExemplo);

        LoteResponse response = loteService.criarLote(request);

        assertThat(response.getOperador()).isEqualTo("joao.silva");
        assertThat(response.getProcesso()).isEqualTo("ABERTURA_CONTA");
        assertThat(response.getStatus()).isEqualTo(StatusLote.PENDENTE);
        assertThat(response.getDocumentos()).hasSize(1);
        verify(loteRepository).save(any(Lote.class));
    }

    // --- listarLotes ---

    @Test
    void listarLotes_semFiltros_deveChamarFindAll() {
        Page<Lote> pagina = new PageImpl<>(List.of(loteExemplo));
        when(loteRepository.findAll(pageable)).thenReturn(pagina);

        PagedResponse<LoteResponse> response = loteService.listarLotes(null, null, pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        verify(loteRepository).findAll(pageable);
        verifyNoMoreInteractions(loteRepository);
    }

    @Test
    void listarLotes_comStatus_deveChamarFindByStatus() {
        Page<Lote> pagina = new PageImpl<>(List.of(loteExemplo));
        when(loteRepository.findByStatus(StatusLote.PENDENTE, pageable)).thenReturn(pagina);

        PagedResponse<LoteResponse> response = loteService.listarLotes(StatusLote.PENDENTE, null, pageable);

        assertThat(response.getContent()).hasSize(1);
        verify(loteRepository).findByStatus(StatusLote.PENDENTE, pageable);
        verifyNoMoreInteractions(loteRepository);
    }

    @Test
    void listarLotes_comOperador_deveChamarFindByOperador() {
        Page<Lote> pagina = new PageImpl<>(List.of(loteExemplo));
        when(loteRepository.findByOperadorContainingIgnoreCase("joao", pageable)).thenReturn(pagina);

        PagedResponse<LoteResponse> response = loteService.listarLotes(null, "joao", pageable);

        assertThat(response.getContent()).hasSize(1);
        verify(loteRepository).findByOperadorContainingIgnoreCase("joao", pageable);
        verifyNoMoreInteractions(loteRepository);
    }

    @Test
    void listarLotes_comStatusEOperador_deveChamarFindByStatusAndOperador() {
        Page<Lote> pagina = new PageImpl<>(List.of(loteExemplo));
        when(loteRepository.findByStatusAndOperadorContainingIgnoreCase(
                StatusLote.PENDENTE, "joao", pageable)).thenReturn(pagina);

        PagedResponse<LoteResponse> response = loteService.listarLotes(StatusLote.PENDENTE, "joao", pageable);

        assertThat(response.getContent()).hasSize(1);
        verify(loteRepository).findByStatusAndOperadorContainingIgnoreCase(StatusLote.PENDENTE, "joao", pageable);
        verifyNoMoreInteractions(loteRepository);
    }

    // --- atualizarStatus ---

    @Test
    void atualizarStatus_deveAlterarStatusERetornarResponse() {
        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteExemplo));
        when(loteRepository.save(loteExemplo)).thenReturn(loteExemplo);

        LoteResponse response = loteService.atualizarStatus(1L, StatusLote.EXPORTADO);

        assertThat(response).isNotNull();
        verify(loteRepository).save(loteExemplo);
    }

    @Test
    void atualizarStatus_loteExportado_deveLancarTransicaoStatusInvalidaException() {
        loteExemplo.setStatus(StatusLote.EXPORTADO);
        when(loteRepository.findById(1L)).thenReturn(Optional.of(loteExemplo));

        assertThatThrownBy(() -> loteService.atualizarStatus(1L, StatusLote.REJEITADO))
                .isInstanceOf(TransicaoStatusInvalidaException.class);

        verify(loteRepository, never()).save(any());
    }

    @Test
    void atualizarStatus_loteInexistente_deveLancarLoteNaoEncontradoException() {
        when(loteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loteService.atualizarStatus(99L, StatusLote.EXPORTADO))
                .isInstanceOf(LoteNaoEncontradoException.class);
    }
}

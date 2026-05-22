package com.documentbatch.service;

import com.documentbatch.dto.*;
import com.documentbatch.entity.Documento;
import com.documentbatch.entity.Lote;
import com.documentbatch.enums.StatusLote;
import com.documentbatch.exception.LoteNaoEncontradoException;
import com.documentbatch.exception.TransicaoStatusInvalidaException;
import com.documentbatch.repository.LoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoteService {

    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional
    public LoteResponse criarLote(CriarLoteRequest request) {
        Lote lote = new Lote();
        lote.setOperador(request.getOperador());
        lote.setProcesso(request.getProcesso());

        for (CriarDocumentoRequest doc : request.getDocumentos()) {
            Documento documento = new Documento();
            documento.setTipo(doc.getTipo());
            documento.setNome(doc.getNome());
            documento.setLote(lote);
            lote.getDocumentos().add(documento);
        }

        return LoteResponse.from(loteRepository.save(lote));
    }

    @Transactional(readOnly = true)
    public PagedResponse<LoteResponse> listarLotes(StatusLote status, String operador, Pageable pageable) {
        Page<Lote> resultado;

        boolean temStatus = status != null;
        boolean temOperador = operador != null && !operador.isBlank();

        if (temStatus && temOperador) {
            resultado = loteRepository.findByStatusAndOperadorContainingIgnoreCase(status, operador, pageable);
        } else if (temStatus) {
            resultado = loteRepository.findByStatus(status, pageable);
        } else if (temOperador) {
            resultado = loteRepository.findByOperadorContainingIgnoreCase(operador, pageable);
        } else {
            resultado = loteRepository.findAll(pageable);
        }

        return PagedResponse.of(resultado.map(LoteResponse::from));
    }

    @Transactional
    public LoteResponse atualizarStatus(Long id, StatusLote novoStatus) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new LoteNaoEncontradoException(id));

        if (lote.getStatus() == StatusLote.EXPORTADO) {
            throw new TransicaoStatusInvalidaException("Lote com status EXPORTADO não pode ser alterado");
        }

        lote.setStatus(novoStatus);
        return LoteResponse.from(loteRepository.save(lote));
    }
}

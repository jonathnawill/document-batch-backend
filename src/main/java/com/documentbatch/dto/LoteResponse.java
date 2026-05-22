package com.documentbatch.dto;

import com.documentbatch.entity.Lote;
import com.documentbatch.enums.StatusLote;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoteResponse {

    private Long id;
    private String operador;
    private String processo;
    private StatusLote status;
    private LocalDateTime dataCriacao;
    private List<DocumentoResponse> documentos;

    public static LoteResponse from(Lote lote) {
        LoteResponse response = new LoteResponse();
        response.setId(lote.getId());
        response.setOperador(lote.getOperador());
        response.setProcesso(lote.getProcesso());
        response.setStatus(lote.getStatus());
        response.setDataCriacao(lote.getDateEntity().getCadastro());
        response.setDocumentos(
            lote.getDocumentos().stream()
                .map(DocumentoResponse::from)
                .toList()
        );
        return response;
    }
}

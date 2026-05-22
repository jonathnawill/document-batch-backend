package com.documentbatch.dto;

import com.documentbatch.entity.Documento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentoResponse {

    private Long id;
    private String tipo;
    private String nome;

    public static DocumentoResponse from(Documento documento) {
        DocumentoResponse response = new DocumentoResponse();
        response.setId(documento.getId());
        response.setTipo(documento.getTipo());
        response.setNome(documento.getNome());
        return response;
    }
}

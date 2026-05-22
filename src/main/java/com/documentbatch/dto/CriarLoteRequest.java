package com.documentbatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CriarLoteRequest {

    @NotBlank(message = "Operador é obrigatório")
    private String operador;

    @NotBlank(message = "Processo é obrigatório")
    private String processo;

    @NotEmpty(message = "O lote deve conter ao menos um documento")
    @Valid
    private List<CriarDocumentoRequest> documentos;
}

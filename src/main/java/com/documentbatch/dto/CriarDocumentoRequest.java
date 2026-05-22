package com.documentbatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarDocumentoRequest {

    @NotBlank(message = "Tipo do documento é obrigatório")
    private String tipo;

    @NotBlank(message = "Nome do documento é obrigatório")
    private String nome;
}

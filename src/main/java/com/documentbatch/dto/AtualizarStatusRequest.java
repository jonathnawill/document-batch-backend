package com.documentbatch.dto;

import com.documentbatch.enums.StatusLote;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AtualizarStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private StatusLote status;
}

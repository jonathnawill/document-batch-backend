package com.documentbatch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateEntity {

    @Column(name = "CADASTRO", updatable = false, nullable = false)
    private LocalDateTime cadastro;

    @Column(name = "ATUALIZACAO", nullable = false)
    private LocalDateTime atualizacao;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo;
}

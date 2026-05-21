package com.documentbatch.entity;

import com.documentbatch.entity.listener.EntityCreateUpdateListener;
import com.documentbatch.enums.StatusLote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lote")
@EntityListeners(EntityCreateUpdateListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Lote implements DateInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operador;

    @Column(nullable = false)
    private String processo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLote status = StatusLote.PENDENTE;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();

    @Embedded
    private DateEntity dateEntity;
}

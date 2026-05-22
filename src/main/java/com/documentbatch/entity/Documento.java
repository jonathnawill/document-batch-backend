package com.documentbatch.entity;

import com.documentbatch.entity.listener.EntityCreateUpdateListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_documento")
@EntityListeners(EntityCreateUpdateListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Documento implements DateInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Embedded
    private DateEntity dateEntity;
}

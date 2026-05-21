package com.documentbatch.repository;

import com.documentbatch.entity.Lote;
import com.documentbatch.enums.StatusLote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Page<Lote> findByStatus(StatusLote status, Pageable pageable);

    Page<Lote> findByOperadorContainingIgnoreCase(String operador, Pageable pageable);

    Page<Lote> findByStatusAndOperadorContainingIgnoreCase(StatusLote status, String operador, Pageable pageable);
}

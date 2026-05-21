package com.documentbatch.entity.listener;

import com.documentbatch.entity.DateEntity;
import com.documentbatch.entity.DateInterface;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class EntityCreateUpdateListener {

    @PrePersist
    protected void onCreate(DateInterface dateInterface) {
        LocalDateTime now = LocalDateTime.now();
        dateInterface.setDateEntity(new DateEntity(now, now, true));
    }

    @PreUpdate
    protected void onUpdate(DateInterface dateInterface) {
        dateInterface.getDateEntity().setAtualizacao(LocalDateTime.now());
    }
}

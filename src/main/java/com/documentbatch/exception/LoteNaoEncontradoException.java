package com.documentbatch.exception;

public class LoteNaoEncontradoException extends RuntimeException {

    public LoteNaoEncontradoException(Long id) {
        super("Lote não encontrado: " + id);
    }
}

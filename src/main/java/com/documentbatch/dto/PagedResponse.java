package com.documentbatch.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;

    public static <T> PagedResponse<T> of(Page<T> pageResult) {
        PagedResponse<T> response = new PagedResponse<>();
        response.content = pageResult.getContent();
        response.totalElements = pageResult.getTotalElements();
        response.totalPages = pageResult.getTotalPages();
        response.page = pageResult.getNumber();
        response.size = pageResult.getSize();
        return response;
    }

    public List<T> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}

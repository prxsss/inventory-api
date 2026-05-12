package com.phuriphat.inventoryapi.common;

import org.springframework.data.domain.Page;

public class PaginationHelper {

    /**
     * Convert Spring Data Page to PaginationResponse
     */
    public static <T> PaginationResponse<T> toPaginationResponse(Page<T> page) {
        PaginationMeta paginationMeta = PaginationMeta.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return PaginationResponse.<T>builder()
                .items(page.getContent())
                .pagination(paginationMeta)
                .build();
    }
}

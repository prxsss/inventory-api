package com.phuriphat.inventoryapi.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryOptionProjection {
    private Long id;

    private String name;
}

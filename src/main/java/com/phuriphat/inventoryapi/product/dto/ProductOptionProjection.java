package com.phuriphat.inventoryapi.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductOptionProjection {
    private Long id;
    private String name;
    private Integer quantity;
}

package com.phuriphat.inventoryapi.stock;

import com.phuriphat.inventoryapi.common.BaseEntity;
import com.phuriphat.inventoryapi.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Integer quantity;

    private String note;
}

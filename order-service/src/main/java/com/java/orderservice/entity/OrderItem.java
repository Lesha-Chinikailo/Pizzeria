package com.java.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orderItems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull(message = "Invalid product id: Empty product id")
    @Column(name = "productId")
    private Long productId;

    @NotNull(message = "Invalid quantity: Empty quantity")
    @Column(name = "quantity")
    private Integer quantity;

}

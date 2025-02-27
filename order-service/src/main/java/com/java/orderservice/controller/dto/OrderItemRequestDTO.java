package com.java.orderservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequestDTO {
    @NotNull(message = "{OrderItemRequestDTO.productId.NotNull}")
    @Column(name = "productId")
    private Long productId;
    @NotNull(message = "{OrderItemRequestDTO.quantity.NotNull}")
    @Column(name = "quantity")
    private Integer quantity;
}

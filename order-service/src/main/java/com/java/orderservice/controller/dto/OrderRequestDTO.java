package com.java.orderservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDTO {
    @NotEmpty(message = "{OrderRequestDTO.orderItems.NotEmpty}")
    @Column(name = "orderItems")
    List<OrderItemRequestDTO> orderItems;
}

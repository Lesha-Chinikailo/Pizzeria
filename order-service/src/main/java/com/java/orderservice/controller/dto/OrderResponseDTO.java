package com.java.orderservice.controller.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    Long id;
    String username;
    Boolean isPaid;
    List<OrderItemResponseDTO> orderItems;
}

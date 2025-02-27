package com.java.orderservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsIdRequestDTO {
    @NotNull(message = "{OrderItemsIdRequestDTO.orderIds.NotNull}")
    @Column(name = "orderIds")
    List<Long> orderIds;
}

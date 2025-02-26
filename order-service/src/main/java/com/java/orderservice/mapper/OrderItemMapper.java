package com.java.orderservice.mapper;

import com.java.orderservice.controller.dto.OrderItemRequestDTO;
import com.java.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem dtoToOrderItem(OrderItemRequestDTO dto);
}

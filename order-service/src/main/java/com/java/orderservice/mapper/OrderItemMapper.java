package com.java.orderservice.mapper;

import com.java.orderservice.controller.dto.OrderItemRequestDTO;
import com.java.orderservice.controller.dto.OrderItemResponseDTO;
import com.java.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem dtoToOrderItem(OrderItemRequestDTO dto);

    OrderItemResponseDTO orderItemToDto(OrderItem orderItem);
}

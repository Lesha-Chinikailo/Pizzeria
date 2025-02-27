package com.java.orderservice.mapper;


import com.java.orderservice.controller.dto.OrderItemRequestDTO;
import com.java.orderservice.controller.dto.OrderItemResponseDTO;
import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.entity.Order;
import com.java.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "orderItems", target = "orderItems", qualifiedByName = "orderItemsToDtos")
    OrderResponseDTO orderToResponseDto(Order order);

    @Mapping(source = "orderItems", target = "orderItems", qualifiedByName = "dtosToOrderItems")
    Order dtoToOrder(OrderRequestDTO dto);

    @Named("dtosToOrderItems")
    default List<OrderItem> dtosToOrderItems(List<OrderItemRequestDTO> dtos) {
        OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);
        return dtos.stream()
                .map(mapper::dtoToOrderItem)
                .toList();
    }

    @Named("orderItemsToDtos")
    default List<OrderItemResponseDTO> orderItemsToDtos(List<OrderItem> orderItems) {
        OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);
        return orderItems
                .stream()
                .map(mapper::orderItemToDto)
                .toList();
    }
}
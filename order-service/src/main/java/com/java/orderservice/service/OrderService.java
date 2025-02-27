package com.java.orderservice.service;

import com.java.orderservice.controller.dto.OrderItemResponseDTO;
import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.entity.Order;
import com.java.orderservice.entity.OrderItem;
import com.java.orderservice.mapper.OrderItemMapper;
import com.java.orderservice.mapper.OrderMapper;
import com.java.orderservice.repository.OrderItemRepository;
import com.java.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    public Long saveNewOrder(OrderRequestDTO dto){
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .toList();

        Order newOrder = Order.buildOrderWithItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);
        return savedOrder.getId();
    }

    public List<OrderResponseDTO> findAllOrders(){
        List<Order> all = orderRepository.findAll();
//        List<OrderResponseDTO> dtos = new ArrayList<>();
//        for (Order order : all) {
//            List<OrderItemResponseDTO> list = order.getOrderItems().stream()
//                    .map(orderItemMapper::orderItemToDto)
//                    .toList();
//            OrderResponseDTO build = OrderResponseDTO.builder()
//                    .id(order.getId())
//                    .username(order.getUsername())
//                    .orderItems(list)
//                    .build();
//            dtos.add(build);
//        }
//        return dtos;


        return all.stream()
                .map(orderMapper::orderToResponseDto)
                .toList();
    }
}

package com.java.orderservice.service;

import com.java.orderservice.controller.dto.OrderIdResponseDTO;
import com.java.orderservice.controller.dto.OrderItemsIdRequestDTO;
import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.entity.Order;
import com.java.orderservice.entity.OrderItem;
import com.java.orderservice.exception.OrderAlreadyPaidException;
import com.java.orderservice.exception.OrderNotFoundException;
import com.java.orderservice.mapper.OrderItemMapper;
import com.java.orderservice.mapper.OrderMapper;
import com.java.orderservice.repository.OrderItemRepository;
import com.java.orderservice.repository.OrderRepository;
import com.java.orderservice.util.MessageExceptionUtil;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    public OrderIdResponseDTO saveNewOrder(OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .toList();

        Order newOrder = Order.buildOrderWithItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);
        return new OrderIdResponseDTO(savedOrder.getId());
    }

    public List<OrderResponseDTO> findAllOrders() {
        List<Order> all = orderRepository.findAll();

        List<OrderResponseDTO> list = all.stream()
                .map(orderMapper::orderToResponseDto)
                .toList();
        return list;
    }

    public List<OrderResponseDTO> findAllOrdersByUsername(String username) {
        List<Order> byUsername = orderRepository.findByUsername(username);

        return byUsername.stream()
                .map(orderMapper::orderToResponseDto)
                .toList();
    }

    public OrderResponseDTO findOrderById(Long id) {
        return orderMapper.orderToResponseDto(
                orderRepository.findById(id)
                        .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(id))));
    }

    public OrderIdResponseDTO addItemsInOrder(Long orderId, OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .collect(Collectors.toList());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        order.addItems(orderItems);
        Order updated = orderRepository.save(order);
        return new OrderIdResponseDTO(updated.getId());
    }

    public OrderIdResponseDTO deleteItemsInOrder(Long orderId, OrderItemsIdRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        List<Long> orderIds = dto.getOrderIds();
        List<OrderItem> orderItems = orderItemRepository.findByIdIn(orderIds);
        order.deleteItems(orderItems);
        order.addItems(orderItems);
        Order updated = orderRepository.save(order);
        return new OrderIdResponseDTO(updated.getId());
    }

    public void deleteOrderById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(id));
        }
        orderRepository.deleteById(id);
    }

    public void payOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(id)));
        if (order.getIsPaid())
            throw new OrderAlreadyPaidException(MessageExceptionUtil.OrderAlreadyPaidWithId.formatted(id));
        order.setIsPaid(true);
        orderRepository.save(order);
    }

    public OrderIdResponseDTO itemIdInOrder(Long productId) {
        List<Order> all = orderRepository.findAll();
        List<Order> list = all.stream()
                .filter(order -> !order.getIsPaid())
                .toList();
        for (Order order : list) {
            List<OrderItem> orderItems = order.getOrderItems();
            List<OrderItem> orderItemsWithProductId = orderItems.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .toList();
            if (!orderItemsWithProductId.isEmpty()) {
                return new OrderIdResponseDTO(order.getId());
            }

        }
        return new OrderIdResponseDTO(-1L);
    }
}

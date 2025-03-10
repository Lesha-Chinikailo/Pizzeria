package com.java.orderservice.service;

import com.java.orderservice.client.ProductServiceClient;
import com.java.orderservice.controller.dto.*;
import com.java.orderservice.entity.Order;
import com.java.orderservice.entity.OrderItem;
import com.java.orderservice.entity.objectKafka.KafkaProductIdIsExistsOrderId;
import com.java.orderservice.entity.objectKafka.KafkaProductOrderId;
import com.java.orderservice.exception.OrderAlreadyPaidException;
import com.java.orderservice.exception.OrderNotFoundException;
import com.java.orderservice.mapper.OrderItemMapper;
import com.java.orderservice.mapper.OrderMapper;
import com.java.orderservice.repository.OrderItemRepository;
import com.java.orderservice.repository.OrderRepository;
import com.java.orderservice.util.MessageExceptionUtil;
import com.java.orderservice.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final KafkaProducerOrderService kafkaProducerService;

    public OrderIdResponseDTO saveNewOrder(OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .toList();


        Order newOrder = Order.buildOrderWithItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);

        checkExistsProductsByOrderItems(savedOrder.getId(), orderItems);

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

    public void setHasDeletedProduct(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        order.setDeletedProductId(productId);
        orderRepository.save(order);
    }

    public OrderIdResponseDTO addItemsInOrder(Long orderId, OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .collect(Collectors.toList());

        checkExistsProductsByOrderItems(orderId, orderItems);

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

    public OrderIdResponseDTO payOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(id)));
        if (order.getIsPaid())
            throw new OrderAlreadyPaidException(MessageExceptionUtil.OrderAlreadyPaidWithId.formatted(id));
        order.setIsPaid(true);
        Order saved = orderRepository.save(order);
        if(!order.getDeletedProductId().equals(-1L)){
            KafkaProductOrderId object = KafkaProductOrderId.builder()
                    .orderId(order.getId())
                    .productId(order.getDeletedProductId())
                    .build();
            kafkaProducerService.sendMessageResponseToProductFromOrderService(object);
        }
        return new OrderIdResponseDTO(saved.getId());
    }

    @Transactional
    public OrderIdResponseDTO findOrderIdByItemId(Long productId) {
        List<Order> all = orderRepository.findAll();
        List<Order> list = all.stream()
                .filter(order -> !order.getIsPaid())
                .toList();
        for (Order order : list) {
            List<OrderItem> orderItems = order.getOrderItems();
            List<OrderItem> orderItemsWithProductId = orderItems
                    .stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .toList();
            if (!orderItemsWithProductId.isEmpty()) {
                return new OrderIdResponseDTO(order.getId());
            }

        }
        return new OrderIdResponseDTO(-1L);
    }


    private void checkExistsProductsByOrderItems(Long orderId, List<OrderItem> orderItems) {
        List<Long> productsIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        checkExistsProductsById(orderId, productsIds);
    }

    @KafkaListener(topics = NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE)
    @Transactional
    public void receiveResponse(ConsumerRecord<String, Object> record) {
        KafkaProductIdIsExistsOrderId value = (KafkaProductIdIsExistsOrderId) record.value();
        log.info("value from kafka in OrderService: {}", value);
        Long orderId = value.getOrderId();
        Long productId = value.getProductId();
        boolean isExists = value.getIsExists();

        if(!isExists){
            Order order = orderRepository.findById(orderId).get();
            OrderItem orderItem = order.getOrderItems()
                    .stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst().get();
            order.deleteItem(orderItem);
            orderRepository.save(order);
        }
    }


    private void checkExistsProductsById(Long orderId, List<Long> productIds) {
        for (Long productId : productIds) {
            checkExistsProductById(orderId, productId);
        }
    }

    private void checkExistsProductById(Long orderId, Long productId) {
        KafkaProductOrderId object = KafkaProductOrderId.builder()
                .productId(productId)
                .orderId(orderId)
                .build();
        kafkaProducerService.sendMessageCheckProductInProductService(object);
    }
}

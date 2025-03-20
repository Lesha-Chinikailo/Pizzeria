package com.java.orderservice.service;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    public OrderResponseDTO saveNewOrder(OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .toList();


        Order newOrder = Order.buildOrderWithItems(orderItems);

        Order savedOrder = orderRepository.save(newOrder);

        checkExistsProductsByOrderItems(savedOrder.getId(), orderItems);

        return orderMapper.orderToResponseDto(savedOrder);
    }

    public List<OrderResponseDTO> findAllOrders() {
        List<Order> all = orderRepository.findAll();

        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().toString();
        if (!role.equals("ROLE_ADMIN")) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return all.stream()
                    .map(orderMapper::orderToResponseDto)
                    .filter(o -> o.getUsername().equals(username))
                    .toList();
        }

        return all.stream()
                .map(orderMapper::orderToResponseDto)
                .toList();
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

    @Transactional
    public void addDeletedProductId(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        order.getDeletedProductIds().add(productId);
        orderRepository.save(order);
    }

    public OrderIdResponseDTO updateOrder(Long orderId, OrderRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));

        List<OrderItem> orderItemsRequest = new ArrayList<>(dto.getOrderItems().stream()
                .map(orderItemMapper::dtoToOrderItem)
                .toList());
        for(OrderItem orderItem : orderItemsRequest) {
            if(orderItem.getId().equals(0L)) {
                orderItem.setId(null);
            }
        }
        List<OrderItem> orderItemsForUpdate = orderItemsRequest.stream()
                .filter(item  -> item.getId() != null)
                .toList();

        List<OrderItem> orderItemsNew = orderItemsRequest.stream()
                .filter(item  -> item.getId() == null)
                .toList();

        List<OrderItem> orderItemsNow = order.getOrderItems();


        List<Long> orderItemsIdForDelete = orderItemsNow
                .stream()
                .map(OrderItem::getId)
                .filter(id -> !orderItemsForUpdate
                        .stream().map(OrderItem::getId)
                        .toList()
                        .contains(id))
                .toList();

        List<Integer> indexForDeleteFromUpdate = new ArrayList<>();
        for (OrderItem orderItem : orderItemsForUpdate) {
            OrderItem item = orderItemsNow.stream()
                    .filter(i -> i.getId().equals(orderItem.getId()))
                    .findFirst().get();
            item.setQuantity(orderItem.getQuantity());
            item.setProductId(orderItem.getProductId());
        }

        OrderIdResponseDTO orderIdResponseDTO = addItemsInOrder(orderId, orderItemsNew);
        deleteItemsInOrder(orderId, orderItemsIdForDelete);
        return orderIdResponseDTO;
    }

    public OrderIdResponseDTO addItemsInOrder(Long orderId, OrderRequestDTO dto) {
        List<OrderItem> orderItems = dto.getOrderItems()
                .stream()
                .map(orderItemMapper::dtoToOrderItem)
                .collect(Collectors.toList());

        return addItemsInOrder(orderId, orderItems);
    }

    public OrderIdResponseDTO addItemsInOrder(Long orderId, List<OrderItem> orderItems) {
        for(OrderItem orderItem : orderItems) {
            if(orderItem.getId() == null || orderItem.getId().equals(0L)) {
                orderItem.setId(null);
            }
        }
        checkExistsProductsByOrderItems(orderId, orderItems);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        order.addItems(orderItems);
        Order updated = orderRepository.save(order);
        return new OrderIdResponseDTO(updated.getId());
    }

    public OrderIdResponseDTO deleteItemsInOrder(Long orderId, List<Long> orderIds) {
        if (orderIds.isEmpty()) {
            return new OrderIdResponseDTO(orderId);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
        List<OrderItem> orderItems = orderItemRepository.findByIdIn(orderIds);

        order.deleteItems(orderItems);
        List<Long> itemIdsDelete = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();

        List<Long> deletedProductIds = order.getDeletedProductIds();
        for(Long productIdForDelete : deletedProductIds) {
            if(itemIdsDelete.contains(productIdForDelete)) {
                sendToRemoveProduct(orderId, productIdForDelete);
            }
        }

        Order updated = orderRepository.save(order);
        return new OrderIdResponseDTO(updated.getId());
    }

    public OrderIdResponseDTO deleteItemsInOrder(Long orderId, OrderItemsIdRequestDTO dto) {
        List<Long> orderIds = dto.getOrderIds();
        return deleteItemsInOrder(orderId, orderIds);
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
        if (!order.getDeletedProductIds().isEmpty()) {
            for(Long productId : order.getDeletedProductIds()) {
                sendToRemoveProduct(order.getId(), productId);
            }
        }
        return new OrderIdResponseDTO(saved.getId());
    }

    private void sendToRemoveProduct(Long orderId, Long productId) {
        KafkaProductOrderId object = KafkaProductOrderId.builder()
                .orderId(orderId)
                .productId(productId)
                .build();
        kafkaProducerService.sendMessageResponseToProductFromOrderService(object);
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

        if (!isExists) {
            Order order = orderRepository.findById(orderId).get();
            OrderItem orderItem = order.getOrderItems()
                    .stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst().get();
            order.deleteItem(orderItem);
            orderRepository.save(order);
//            removeDuplicateInOrder(orderId);
        }
    }

//    private void removeDuplicateInOrder(Long orderId){
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
//        List<OrderItem> orderItems = order.getOrderItems();
//        Set<Long> distinctProductsId = new HashSet<>();
//
//        List<OrderItem> orderItemsForDelete = orderItems
//                .stream()
//                .filter(n -> !distinctProductsId.add(n.getProductId()))
//                .toList();
//
//        Map<Long, Integer> countItemByProductId = new HashMap<>();
//        for (OrderItem orderItem : orderItemsForDelete) {
//            countItemByProductId.put(orderItem.getProductId(), orderItem.getQuantity());
//            orderItems.remove(orderItem);
//        }
//
//        for(var item : countItemByProductId.entrySet()){
//            Optional<OrderItem> itemMaybe = orderItems.stream().filter(i -> i.getProductId().equals(item.getKey()))
//                    .findFirst();
//            if (itemMaybe.isPresent()) {
//                OrderItem orderItemExists = itemMaybe.get();
//                orderItemExists.setQuantity(orderItemExists.getQuantity() + item.getValue());
//            }
//        }
//        orderRepository.save(order);
//    }



//    public OrderIdResponseDTO updateOrder(Long orderId, OrderRequestDTO dto) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(MessageExceptionUtil.UnableFindOrderById.formatted(orderId)));
//
//        List<OrderItem> orderItemsForUpdate = new ArrayList<>(dto.getOrderItems().stream()
//                .map(orderItemMapper::dtoToOrderItem)
//                .toList());
//
//        List<OrderItem> orderItemsNow = order.getOrderItems();
//
//        Map<Long, OrderItem> currentItemsMap = orderItemsNow.stream()
//                .collect(Collectors.toMap(OrderItem::getProductId, Function.identity()));
//
//        List<Long> orderItemsIdForDelete = orderItemsNow
//                .stream()
//                .map(OrderItem::getId)
//                .filter(id -> !orderItemsForUpdate
//                        .stream().map(OrderItem::getId)
//                        .toList()
//                        .contains(id))
//                .toList();
//
//        List<Integer> indexForDeleteFromUpdate = new ArrayList<>();
////        for (OrderItem orderItem : orderItemsForUpdate) {
////            OrderItem item = currentItemsMap.get(orderItem.getProductId());
////            if (item != null) {
////                item.setQuantity(orderItem.getQuantity());
////                Optional<OrderItem> first = orderItemsForUpdate.stream()
////                        .filter(i -> i.getProductId().equals(item.getProductId()))
////                        .findFirst();
////                List<Long> productIds = orderItemsForUpdate.stream()
////                        .map(OrderItem::getProductId)
////                        .toList();
////                int index = productIds.indexOf(first.get().getProductId());
////                indexForDeleteFromUpdate.add(index);
////            }
////        }
////        for(int index : indexForDeleteFromUpdate) {
////            orderItemsForUpdate.remove(index);
////        }
//
//        OrderIdResponseDTO orderIdResponseDTO = addItemsInOrder(orderId, orderItemsForUpdate);
//        deleteItemsInOrder(orderId, orderItemsIdForDelete);
//        return orderIdResponseDTO;
//    }


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

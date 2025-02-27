package com.java.orderservice.controller;

import com.java.orderservice.controller.dto.OrderItemsIdRequestDTO;
import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> addOrder(@RequestBody OrderRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.saveNewOrder(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUsername(@PathVariable String username) {
        return ResponseEntity.ok(orderService.findAllOrdersByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @PutMapping("/addItems/{id}")
    public ResponseEntity<Long> addItemsInOrder(@PathVariable Long id, @RequestBody OrderRequestDTO dto) {
        return ResponseEntity.ok(
                orderService.addItemsInOrder(id, dto));
    }

    @PutMapping("/deleteItems/{id}")
    public ResponseEntity<Long> deleteItemsInOrder(@PathVariable Long id, @RequestBody OrderItemsIdRequestDTO dto) {
        return ResponseEntity.ok(orderService.deleteItemsInOrder(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}

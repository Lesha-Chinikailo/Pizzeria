package com.java.orderservice.controller;

import com.java.orderservice.controller.dto.OrderIdResponseDTO;
import com.java.orderservice.controller.dto.OrderItemsIdRequestDTO;
import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@CrossOrigin(origins = "http://localhost:4200/")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> addOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.saveNewOrder(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUsername(@PathVariable String username) {
        return ResponseEntity.ok(orderService.findAllOrdersByUsername(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderIdResponseDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequestDTO dto) {
        return ResponseEntity.ok(
                orderService.updateOrder(id, dto));
    }

    @PutMapping("/addItems/{id}")
    public ResponseEntity<OrderIdResponseDTO> addItemsInOrder(@PathVariable Long id, @Valid @RequestBody OrderRequestDTO dto) {
        return ResponseEntity.ok(
                orderService.addItemsInOrder(id, dto));
    }

    @PutMapping("/deleteItems/{id}")
    public ResponseEntity<OrderIdResponseDTO> deleteItemsInOrder(@PathVariable Long id, @Valid @RequestBody OrderItemsIdRequestDTO dto) {
        return ResponseEntity.ok(orderService.deleteItemsInOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pay/{id}")
    public ResponseEntity<OrderIdResponseDTO> payOrder(@PathVariable Long id){
        return ResponseEntity.ok().body(orderService.payOrder(id));
    }

    @GetMapping("/productId/{productId}")
    public ResponseEntity<OrderIdResponseDTO> getOrderIdByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(orderService.findOrderIdByItemId(productId));
    }
}

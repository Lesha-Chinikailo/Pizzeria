package com.java.orderservice.controller;

import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.controller.dto.OrderResponseDTO;
import com.java.orderservice.service.OrderService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Long addOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.saveNewOrder(dto);
    }

    @GetMapping
    public List<OrderResponseDTO> getOrders() {
        return orderService.findAllOrders();
    }
}

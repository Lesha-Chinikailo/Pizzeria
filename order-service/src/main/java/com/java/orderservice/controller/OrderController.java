package com.java.orderservice.controller;

import com.java.orderservice.controller.dto.OrderRequestDTO;
import com.java.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Long addOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.saveNewOrder(dto);
    }
}

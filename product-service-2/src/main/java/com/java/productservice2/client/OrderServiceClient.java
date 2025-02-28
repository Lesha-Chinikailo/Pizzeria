package com.java.productservice2.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${order-service.url}", configuration = FeignClientsConfiguration.class)
public interface OrderServiceClient {
    @GetMapping("/productId/{productId}")
    ResponseEntity<Long> getOrderIdByProductId(@PathVariable Long productId);
}

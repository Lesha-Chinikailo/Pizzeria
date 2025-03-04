package com.java.orderservice.client;

import com.java.orderservice.controller.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product-service.url}", configuration = FeignClientsConfiguration.class)
public interface ProductServiceClient {
    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable Long id);
}
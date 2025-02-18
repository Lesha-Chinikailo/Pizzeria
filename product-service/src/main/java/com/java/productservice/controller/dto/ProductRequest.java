package com.java.productservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotNull(message = "{productRequest.categoryId.NotNull}")
    @Column(name = "categoryId")
    private Long categoryId;

    @NotBlank(message = "{productRequest.name.NotBlank}")
    @Column(name = "name")
    @Size(min = 0, max = 50, message = "{productRequest.name.Size}")
    private String name;

    @NotNull(message = "{productRequest.price.NotNull}")
    @Column(name = "price")
    @Min(value = 0, message = "{productRequest.price.Min}")
    private Double price;

    @NotNull(message = "{productRequest.quantity.NotNull}")
    @Column(name = "quantity")
    @Min(value = 0, message = "{productRequest.quantity.Min}")
    private Double quantity;//масса нетто в граммах или объем в литрах
}

package com.java.productservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NonNull
    @Column(name = "category")
    @Size(max = 50)
    private String category;

    @NonNull
    @Column(name = "name")
    @Size(max = 50)
    private String name;

    @NonNull
    @Column(name = "price")
    private Double price;

    @NonNull
    @Column(name = "quantity")
    private Double quantity;//масса нетто в граммах или объем в литрах

    @NonNull
    @Column(name = "dateTimeOfManufacture")
    private LocalDateTime dateTimeOfManufacture;
}

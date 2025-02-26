package com.java.productservice.controller.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {

    @NotNull
    @Column(name = "category")
    @Size(max = 50)
    private String categoryId;

    @NotNull
    @Column(name = "name")
    @Size(max = 50)
    private String name;

    @NotNull
    @Column(name = "price")
    private Double price;

    @NotNull
    @Column(name = "quantity")
    private Double quantity;//масса нетто в граммах или объем в литрах

    @NotNull
    @Column(name = "dateTimeOfManufacture")
    private LocalDateTime dateTimeOfManufacture;
}

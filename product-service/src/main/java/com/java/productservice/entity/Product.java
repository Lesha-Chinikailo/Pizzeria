package com.java.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "categoryId")
    private Long categoryId;

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

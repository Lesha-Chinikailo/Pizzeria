package com.java.productservice.mapper;

import com.java.productservice.controller.dto.ProductRequest;
import com.java.productservice.controller.dto.ProductResponse;
import com.java.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product productRequestToProduct(ProductRequest productRequest);

    ProductResponse productToProductResponse(Product product);
}

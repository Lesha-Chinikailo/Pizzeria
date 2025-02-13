package com.java.productservice.service;

import com.java.productservice.client.CategoryServiceClient;
import com.java.productservice.controller.dto.ProductRequest;
import com.java.productservice.controller.dto.ProductResponse;
import com.java.productservice.entity.Product;
import com.java.productservice.exception.ProductNotFoundException;
import com.java.productservice.mapper.ProductMapper;
import com.java.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryServiceClient categoryServiceClient;

    public Long createProduct(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        return productRepository.save(product).getId();
    }

    public ProductResponse getProductById(Long id) {
        return productMapper.productToProductResponse(
                productRepository.findById(id)
                        .orElseThrow(() -> new ProductNotFoundException("Unable to find product with id: " + id)));
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> all = productRepository.findAll();
        return all.stream()
                .map(productMapper::productToProductResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        Long id = categoryServiceClient.getCategoryIdByName(category).getBody();
        List<Product> byCategoryId = productRepository.findByCategoryId(id);
        return byCategoryId.stream()
                .map(productMapper::productToProductResponse)
                .toList();
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product productInfo = productMapper.productRequestToProduct(productRequest);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Unable to find product with id: " + id));

        product.setName(productInfo.getName());
        product.setPrice(productInfo.getPrice());
        product.setCategoryId(productInfo.getCategoryId());
        product.setDateTimeOfManufacture(productInfo.getDateTimeOfManufacture());
        product.setQuantity(productInfo.getQuantity());

        return productMapper.productToProductResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Unable to find product with id: " + id);
        }
        productRepository.deleteById(id);
    }

}

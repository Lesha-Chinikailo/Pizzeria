package com.java.productservice2.service;

import com.java.productservice2.client.OrderServiceClient;
import com.java.productservice2.controller.dto.ProductIdResponse;
import com.java.productservice2.controller.dto.ProductRequest;
import com.java.productservice2.controller.dto.ProductResponse;
import com.java.productservice2.entity.Product;
import com.java.productservice2.exception.CategoryNotFoundException;
import com.java.productservice2.exception.ProductIsTakenException;
import com.java.productservice2.exception.ProductNotFoundException;
import com.java.productservice2.mapper.ProductMapper;
import com.java.productservice2.repository.ProductRepository;
import com.java.productservice2.util.MessageExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderServiceClient orderServiceClient;
    private final CategoryService categoryService;

    public ProductIdResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        if (!categoryService.existsCategory(product.getCategoryId()))
            throw new CategoryNotFoundException(MessageExceptionUtil.CategoryNotFoundWithIdByAddProduct.formatted(product.getCategoryId()));
        product.setDateTimeOfManufacture(LocalDateTime.now());
        Product saved = productRepository.save(product);
        return new ProductIdResponse(saved.getId());
    }

    public ProductResponse getProductById(Long id) {
        return productMapper.productToProductResponse(
                productRepository.findById(id)
                        .orElseThrow(() -> new ProductNotFoundException(MessageExceptionUtil.UnableFindProductById.formatted(id))));
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> all = productRepository.findAll();
        return all.stream()
                .map(productMapper::productToProductResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        List<Product> byCategoryId = productRepository.findByCategoryId(categoryId);
        return byCategoryId.stream()
                .map(productMapper::productToProductResponse)
                .toList();
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(MessageExceptionUtil.UnableFindProductById.formatted(id)));

        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setCategoryId(productRequest.getCategoryId());
        product.setQuantity(productRequest.getQuantity());

        return productMapper.productToProductResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(MessageExceptionUtil.UnableFindProductById.formatted(id));
        }
        Long body = orderServiceClient.getOrderIdByProductId(id).getBody();
        if (body == -1) {
            throw new ProductIsTakenException(MessageExceptionUtil.ProductIsTakenWithId.formatted(id));
        }

        productRepository.deleteById(id);
    }

}

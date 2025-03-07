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
import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderServiceClient orderServiceClient;
    private final CategoryService categoryService;
    private final KafkaProducerProductService kafkaProducerService;

    public ProductIdResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        if (!categoryService.existsCategory(product.getCategoryId()))
            throw new CategoryNotFoundException(MessageExceptionUtil.CategoryNotFoundWithIdByAddProduct.formatted(product.getCategoryId()));
        product.setDateTimeOfManufacture(LocalDateTime.now());
        product.setIsAvailable(true);
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

    @KafkaListener(topics = NameServiceUtil.RESPONSE_FROM_ORDER_SERVICE)
    public void receiveResponse(ConsumerRecord<String, String> record) {
        String message = record.value();
        System.out.println("message in ProductService: " + message);

        Long orderId;
        Long productId;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(message);
            orderId = (Long) json.get("orderId");
            productId = (Long) json.get("productId");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if(orderId != -1){
            JSONObject json = new JSONObject();
            json.put("productId", productId);
            kafkaProducerService.sendMessageCheckProductInOrderService(json.toJSONString());
        }
        else{
            productRepository.deleteById(productId);
        }
    }

    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ProductNotFoundException(MessageExceptionUtil.UnableFindProductById.formatted(id));
        }
        Product product = productRepository.findById(id).get();
        product.setIsAvailable(false);
        productRepository.save(product);

        JSONObject json = new JSONObject();
        json.put("productId", id);

        kafkaProducerService.sendMessageCheckProductInOrderService(json.toJSONString());
    }

}

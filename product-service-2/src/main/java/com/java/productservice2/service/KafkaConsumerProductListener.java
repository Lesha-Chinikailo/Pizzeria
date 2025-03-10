package com.java.productservice2.service;

import com.java.productservice2.controller.dto.ProductResponse;
import com.java.productservice2.entity.objectKafka.CustomKafkaObject;
import com.java.productservice2.entity.objectKafka.KafkaProductIdIsExistsOrderId;
import com.java.productservice2.entity.objectKafka.KafkaProductOrderId;
import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor()
class KafkaConsumerProductListener {

    private final KafkaProducerProductService kafkaProducerService;
    private final ProductService productService;

    @KafkaListener(topics = NameServiceUtil.CHECK_PRODUCT_IN_PRODUCT_SERVICE)
    public void receiveResponseToOrderService(ConsumerRecord<String, CustomKafkaObject> record) {
        KafkaProductOrderId value = (KafkaProductOrderId) record.value();
        Long orderId = value.getOrderId();
        Long productId = value.getProductId();
        Boolean isExists;

        try{
            ProductResponse productById = productService.getProductById(productId);
            isExists = productById.getIsAvailable();
        }
        catch (RuntimeException e){
            isExists = false;
        }
        KafkaProductIdIsExistsOrderId object = KafkaProductIdIsExistsOrderId.builder()
                .productId(productId)
                .orderId(orderId)
                .isExists(isExists)
                .build();
        kafkaProducerService.sendMessageResponseToOrderFromProductService(object);
    }


}
package com.java.productservice2.service;

import com.java.productservice2.controller.dto.ProductResponse;
import com.java.productservice2.entity.objectKafka.CustomKafkaObject;
import com.java.productservice2.entity.objectKafka.KafkaProductIdIsExistsOrderId;
import com.java.productservice2.entity.objectKafka.KafkaProductOrderId;
import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
//        try {
//            JSONObject json = (JSONObject) new JSONParser().parse(message);
//            orderId = (Long) json.get("orderId");
//            productId = (Long) json.get("productId");
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }

        try{
            ProductResponse productById = productService.getProductById(productId);
            isExists = productById.getIsAvailable();
        }
        catch (RuntimeException e){
            isExists = false;
        }
//        JSONObject json = new JSONObject();
//        json.put("productId", productId);
//        json.put("orderId", orderId);
//        json.put("isExists", isExists);
        KafkaProductIdIsExistsOrderId object = KafkaProductIdIsExistsOrderId.builder()
                .productId(productId)
                .orderId(orderId)
                .isExists(isExists)
                .build();
//        KafkaObjectProductIdIsExistsOrderId object = new KafkaObjectProductIdIsExistsOrderId(productId, orderId, isExists);

        kafkaProducerService.sendMessageResponseToOrderFromProductService(object);
    }


}
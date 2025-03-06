package com.java.orderservice.service;

import com.java.orderservice.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerOrderService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageCheckProductInProductService(String message) {
        sendMessage(NameServiceUtil.CHECK_PRODUCT_IN_PRODUCT_SERVICE, message);
    }

    public void sendMessageResponseToProductFromOrderService(String message) {
        sendMessage(NameServiceUtil.RESPONSE_FROM_ORDER_SERVICE, message);
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

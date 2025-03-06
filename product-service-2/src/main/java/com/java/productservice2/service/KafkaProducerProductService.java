package com.java.productservice2.service;

import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerProductService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageCheckProductInOrderService(String message) {
        sendMessage(NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE, message);
    }

    public void sendMessageResponseToOrderFromProductService(String message) {
        sendMessage(NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE, message);
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

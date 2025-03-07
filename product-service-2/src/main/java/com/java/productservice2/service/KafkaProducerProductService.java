package com.java.productservice2.service;

import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerProductService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<SendResult<String, String>> sendMessageCheckProductInOrderService(String message) {
        return sendMessage(NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE, message);
    }

    public CompletableFuture<SendResult<String, String>> sendMessageResponseToOrderFromProductService(String message) {
        return sendMessage(NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE, message);
    }

    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String message) {
        return kafkaTemplate.send(topic, message).toCompletableFuture();
    }
}

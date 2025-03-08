package com.java.productservice2.service;

import com.java.productservice2.entity.objectKafka.CustomKafkaObject;
import com.java.productservice2.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerProductService {

    private final KafkaTemplate<String, CustomKafkaObject> kafkaTemplate;

    public void sendMessageCheckProductInOrderService(CustomKafkaObject object) {
        sendMessage(NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE, object);
    }

    public void sendMessageResponseToOrderFromProductService(CustomKafkaObject object) {
        sendMessage(NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE, object);
    }

    public void sendMessage(String topic, CustomKafkaObject object) {
        kafkaTemplate.send(topic, object);
    }
}

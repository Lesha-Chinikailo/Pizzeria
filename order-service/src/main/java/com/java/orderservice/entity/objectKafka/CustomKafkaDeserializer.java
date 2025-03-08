package com.java.orderservice.entity.objectKafka;

import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class CustomKafkaDeserializer implements Deserializer<CustomKafkaObject> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public CustomKafkaObject deserialize(String topic, byte[] data) {
        CustomKafkaObject object = null;
        try {
//            Object o = objectMapper.readValue(data, CustomKafkaObject.class);
            // Deserialize JSON data into CustomKafkaObject
            Object o = objectMapper.readValue(data, Object.class);
            Map<String, Object> map = (HashMap<String, Object>) o;
            int size = map.size();
            switch (size) {
                case 1:
                    Long productId = ((Integer) map.get("productId")).longValue();
                    object = new KafkaProductId(productId);
                    break;
                case 2:
                    productId = ((Integer) map.get("productId")).longValue();
                    Long orderId = ((Integer) map.get("orderId")).longValue();
                    object = KafkaProductOrderId.builder()
                            .productId(productId)
                            .orderId(orderId)
                            .build();
                    break;
                case 3:
                    productId = ((Integer) map.get("productId")).longValue();
                    orderId = ((Integer) map.get("orderId")).longValue();
                    Boolean isExists = (Boolean) map.get("isExists");
                    object = KafkaProductIdIsExistsOrderId.builder()
                            .productId(productId)
                            .orderId(orderId)
                            .isExists(isExists)
                            .build();
                    break;
            }

//            object = objectMapper.readValue(data, CustomKafkaObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
        return object;
    }

    @Override
    public void close() {}
}


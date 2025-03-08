package com.java.orderservice.config;

import com.java.orderservice.entity.objectKafka.CustomKafkaDeserializer;
import com.java.orderservice.entity.objectKafka.CustomKafkaObject;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public Deserializer<CustomKafkaObject> customKafkaDeserializer() {
        return new CustomKafkaDeserializer();
    }
}

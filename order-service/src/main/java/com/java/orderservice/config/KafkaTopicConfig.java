package com.java.orderservice.config;

import com.java.orderservice.util.NameServiceUtil;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic productServiceTopic() {
        return TopicBuilder.name(NameServiceUtil.CHECK_PRODUCT_IN_PRODUCT_SERVICE).build();
    }

    @Bean
    public NewTopic orderServiceTopic() {
        return TopicBuilder.name(NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE).build();
    }
}

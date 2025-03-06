package com.java.productservice2.service;

import com.java.productservice2.util.NameServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
class KafkaConsumerProductListener {

    private BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);

    @Autowired
    private KafkaProducerProductService kafkaProducerService;

    @KafkaListener(topics = NameServiceUtil.RESPONSE_FROM_ORDER_SERVICE)
    public void receiveResponse(ConsumerRecord<String, String> record) {
        String message = record.value();
//        kafkaProducerService.sendMessageCheckProductInOrderService(message + " this is the message");
    }

    @KafkaListener(topics = NameServiceUtil.CHECK_PRODUCT_IN_PRODUCT_SERVICE)
    public void receiveResponseToOrderService(ConsumerRecord<String, String> record) {
        String message = record.value();
        //logic
        kafkaProducerService.sendMessageResponseToOrderFromProductService(message + " this is the message");
    }


}
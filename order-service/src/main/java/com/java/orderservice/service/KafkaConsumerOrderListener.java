package com.java.orderservice.service;

import com.java.orderservice.util.NameServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class KafkaConsumerOrderListener {

    private BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
    @Autowired
    private KafkaProducerOrderService kafkaProducerService;

    @KafkaListener(topics = NameServiceUtil.RESPONSE_FROM_PRODUCT_SERVICE)
    public void receiveResponse(ConsumerRecord<String, String> record) {
        String message = record.value();
        System.out.println("Received response: " + message);
        responseQueue.offer(message);
    }

    public boolean checkExistsProduct(){
        try {
            String response = responseQueue.poll(10, TimeUnit.SECONDS);
            System.out.println("\n\n\n\n response: " + response + "\n\n\n\n");
            return false;
        } catch (InterruptedException e) {
            System.out.println("\n\n\n\n exception \n\n\n\n");
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE)
    public void receiveResponseAndSendToProductService(ConsumerRecord<String, String> record) {
        String message = record.value();
        //logic
        kafkaProducerService.sendMessageResponseToProductFromOrderService(message + " response from order-service");
    }

}
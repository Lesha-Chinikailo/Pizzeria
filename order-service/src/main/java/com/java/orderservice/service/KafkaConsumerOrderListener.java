package com.java.orderservice.service;

import com.java.orderservice.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
@RequiredArgsConstructor()
class KafkaConsumerOrderListener {

    private BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
    private final KafkaProducerOrderService kafkaProducerService;
    private final OrderService orderService;

    @KafkaListener(topics = NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE)
    public void receiveResponseAndSendToProductService(ConsumerRecord<String, String> record) {
        String message = record.value();
        System.out.println("message in KafkaConsumerOrderListener: " + message);

        Long productId;
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(message);
            productId = (Long) json.get("productId");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Long orderId = orderService.findOrderIdByItemId(productId).getOrderId();
        if(orderId.equals(-1L)){
            JSONObject json = new JSONObject();
            json.put("productId", productId);
            json.put("orderId", orderId);
            kafkaProducerService.sendMessageResponseToProductFromOrderService(json.toJSONString());
        }
        else{
            orderService.setHasDeletedProduct(orderId, productId);
        }
    }

}
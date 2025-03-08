package com.java.orderservice.service;

import com.java.orderservice.entity.objectKafka.CustomKafkaObject;
import com.java.orderservice.entity.objectKafka.KafkaProductId;
import com.java.orderservice.entity.objectKafka.KafkaProductOrderId;
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
    public void receiveResponseAndSendToProductService(ConsumerRecord<String, Object> record) {
        KafkaProductId value = (KafkaProductId) record.value();
        log.info("value from kafka in KafkaConsumerOrderListener: {}", value);

        Long productId = value.getProductId();
//        try {
//            JSONObject json = (JSONObject) new JSONParser().parse(value);
//            productId = (Long) json.get("productId");
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }

        Long orderId = orderService.findOrderIdByItemId(productId).getOrderId();
        if(orderId.equals(-1L)){
//            JSONObject json = new JSONObject();
//            json.put("productId", productId);
//            json.put("orderId", orderId);
            KafkaProductOrderId object = KafkaProductOrderId.builder()
                    .orderId(orderId)
                    .productId(productId)
                    .build();

            kafkaProducerService.sendMessageResponseToProductFromOrderService(object);
        }
        else{
            orderService.setHasDeletedProduct(orderId, productId);
        }
    }

}
package com.java.orderservice.service;

import com.java.orderservice.entity.objectKafka.KafkaProductId;
import com.java.orderservice.entity.objectKafka.KafkaProductOrderId;
import com.java.orderservice.util.NameServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor()
class KafkaConsumerOrderListener {

    private final KafkaProducerOrderService kafkaProducerService;
    private final OrderService orderService;

    @KafkaListener(topics = NameServiceUtil.CHECK_PRODUCT_IN_ORDER_SERVICE)
    public void receiveResponseAndSendToProductService(ConsumerRecord<String, Object> record) {
        KafkaProductId value = (KafkaProductId) record.value();
        log.info("value from kafka in KafkaConsumerOrderListener: {}", value);

        Long productId = value.getProductId();

        Long orderId = orderService.findOrderIdByItemId(productId).getOrderId();
        if(orderId.equals(-1L)){
            KafkaProductOrderId object = KafkaProductOrderId.builder()
                    .orderId(orderId)
                    .productId(productId)
                    .build();

            kafkaProducerService.sendMessageResponseToProductFromOrderService(object);
        }
        else{
            orderService.addDeletedProductId(orderId, productId);
        }
    }

}
package com.java.orderservice.entity.objectKafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaProductIdIsExistsOrderId extends CustomKafkaObject{
    private Long productId;
    private Long orderId;
    private Boolean isExists;
}

package com.java.productservice2.entity.objectKafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaProductId extends CustomKafkaObject{
    private Long productId;
}

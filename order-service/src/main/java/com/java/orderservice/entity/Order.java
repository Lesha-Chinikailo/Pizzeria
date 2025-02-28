package com.java.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDateTime orderDate;

    @Builder.Default
    private Boolean isPaid = false;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order buildOrderWithItems(List<OrderItem> orderItems) {
        Order newOrder = Order.builder()
                .orderDate(LocalDateTime.now())
                .username(SecurityContextHolder.getContext().getAuthentication().getName())
                .build();
        for (OrderItem orderItem : orderItems) {
            newOrder.addItem(orderItem);
        }
        return newOrder;
    }

    public void addItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void addItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            addItem(orderItem);
        }
    }

    public void deleteItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            deleteItem(orderItem);
        }
    }

    private void deleteItem(OrderItem orderItem) {
        orderItem.setOrder(null);
        orderItems.remove(orderItem);
    }
}

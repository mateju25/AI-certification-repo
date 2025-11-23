package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.event.OrderCompletedEvent;
import com.example.ecommerce.event.OrderCreatedEvent;
import com.example.ecommerce.event.OrderExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderCreated(Order order) {
        try {
            OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getTotal(),
                LocalDateTime.now()
            );

            kafkaTemplate.send(TOPIC, "order-created-" + order.getId(), event)
                .thenAccept(result -> log.info("Published OrderCreatedEvent for order: {}", order.getId()))
                .exceptionally(ex -> {
                    log.error("Failed to publish OrderCreatedEvent for order: {}", order.getId(), ex);
                    return null;
                });
        } catch (Exception e) {
            log.error("Error publishing OrderCreatedEvent for order: {}", order.getId(), e);
        }
    }

    public void publishOrderCompleted(Long orderId) {
        try {
            OrderCompletedEvent event = new OrderCompletedEvent(orderId, LocalDateTime.now());
            kafkaTemplate.send(TOPIC, "order-completed-" + orderId, event)
                .thenAccept(result -> log.info("Published OrderCompletedEvent for order: {}", orderId))
                .exceptionally(ex -> {
                    log.error("Failed to publish OrderCompletedEvent for order: {}", orderId, ex);
                    return null;
                });
        } catch (Exception e) {
            log.error("Error publishing OrderCompletedEvent for order: {}", orderId, e);
        }
    }

    public void publishOrderExpired(Long orderId) {
        try {
            OrderExpiredEvent event = new OrderExpiredEvent(orderId, LocalDateTime.now());
            kafkaTemplate.send(TOPIC, "order-expired-" + orderId, event)
                .thenAccept(result -> log.info("Published OrderExpiredEvent for order: {}", orderId))
                .exceptionally(ex -> {
                    log.error("Failed to publish OrderExpiredEvent for order: {}", orderId, ex);
                    return null;
                });
        } catch (Exception e) {
            log.error("Error publishing OrderExpiredEvent for order: {}", orderId, e);
        }
    }
}

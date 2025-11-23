package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.event.OrderCreatedEvent;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "order-processor-group")
    public void handleOrderEvents(@Payload Map<String, Object> message) {
        try {
            // Check if this is an OrderCreatedEvent (has userId field)
            if (!message.containsKey("userId")) {
                // Not an OrderCreatedEvent, ignore
                return;
            }

            // Convert Map to OrderCreatedEvent
            OrderCreatedEvent event = objectMapper.convertValue(message, OrderCreatedEvent.class);
            handleOrderCreated(event);

        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    private void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received OrderCreatedEvent for order: {}", event.getOrderId());

            Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.getOrderId()));

            // Update status to PROCESSING
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
            log.info("Order {} status updated to PROCESSING", order.getId());

            // Simulate payment processing (5 second delay)
            Thread.sleep(5000);

            // 50% success rate for payment
            boolean paymentSuccess = Math.random() < 0.5;

            if (paymentSuccess) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                log.info("Order {} payment successful - status updated to COMPLETED", order.getId());

                // Publish OrderCompleted event
                eventPublisher.publishOrderCompleted(order.getId());
            } else {
                log.info("Order {} payment failed - remains in PROCESSING", order.getId());
                // Order stays in PROCESSING - will be expired by scheduler if not retried
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for order: {}", event.getOrderId(), e);
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order: {}", event.getOrderId(), e);
        }
    }
}

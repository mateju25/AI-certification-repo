package com.example.ecommerce.service;

import com.example.ecommerce.entity.Notification;
import com.example.ecommerce.event.OrderCompletedEvent;
import com.example.ecommerce.event.OrderExpiredEvent;
import com.example.ecommerce.repository.NotificationRepository;
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
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void handleOrderEvents(org.apache.kafka.clients.consumer.ConsumerRecord<String, Map<String, Object>> record) {
        try {
            String key = record.key();
            Map<String, Object> message = record.value();

            // Check if this has userId field (OrderCreatedEvent) - ignore it
            if (message.containsKey("userId")) {
                return;
            }

            // Use the key to determine event type
            if (key != null && key.startsWith("order-completed-")) {
                OrderCompletedEvent event = objectMapper.convertValue(message, OrderCompletedEvent.class);
                handleOrderCompleted(event);
            } else if (key != null && key.startsWith("order-expired-")) {
                OrderExpiredEvent event = objectMapper.convertValue(message, OrderExpiredEvent.class);
                handleOrderExpired(event);
            }

        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    private void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            log.info("Received OrderCompletedEvent for order: {}", event.getOrderId());

            // Simulate email notification (log to console)
            String emailMessage = String.format(
                "FAKE EMAIL: Order %d has been completed successfully!",
                event.getOrderId()
            );
            log.info("📧 {}", emailMessage);

            // Save notification to database (audit trail)
            Notification notification = new Notification();
            notification.setOrderId(event.getOrderId());
            notification.setType("ORDER_COMPLETED");
            notification.setMessage(emailMessage);
            notificationRepository.save(notification);

            log.info("Notification saved to database for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Error handling OrderCompletedEvent for order: {}", event.getOrderId(), e);
        }
    }

    private void handleOrderExpired(OrderExpiredEvent event) {
        try {
            log.info("Received OrderExpiredEvent for order: {}", event.getOrderId());

            // No email for expired orders, just save to database
            Notification notification = new Notification();
            notification.setOrderId(event.getOrderId());
            notification.setType("ORDER_EXPIRED");
            notification.setMessage(String.format("Order %d has expired after 10 minutes in PROCESSING state",
                                                  event.getOrderId()));
            notificationRepository.save(notification);

            log.info("Expiration notification saved to database for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Error handling OrderExpiredEvent for order: {}", event.getOrderId(), e);
        }
    }
}

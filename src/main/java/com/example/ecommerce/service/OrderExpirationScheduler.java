package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationScheduler {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOldOrders() {
        try {
            LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(10);
            List<Order> expiredOrders = orderRepository.findByStatusAndUpdatedAtBefore(
                OrderStatus.PROCESSING,
                expirationThreshold
            );

            if (expiredOrders.isEmpty()) {
                log.debug("No orders to expire");
                return;
            }

            log.info("Found {} orders to expire", expiredOrders.size());

            for (Order order : expiredOrders) {
                order.setStatus(OrderStatus.EXPIRED);
                orderRepository.save(order);

                // Publish OrderExpired event
                eventPublisher.publishOrderExpired(order.getId());

                log.info("Order {} expired (was in PROCESSING for > 10 minutes)", order.getId());
            }

        } catch (Exception e) {
            log.error("Error in expireOldOrders scheduled job", e);
        }
    }
}

package com.example.ecommerce.dto.order;

import com.example.ecommerce.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

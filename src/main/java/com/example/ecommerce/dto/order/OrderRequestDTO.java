package com.example.ecommerce.dto.order;

import com.example.ecommerce.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemDTO> items;
}

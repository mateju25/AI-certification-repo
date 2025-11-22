package com.example.ecommerce.dto.order;

import com.example.ecommerce.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDTO {

    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to 0")
    private BigDecimal total;

    private OrderStatus status;

    @Valid
    private List<OrderItemDTO> items;
}

package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ErrorResponseDTO;
import com.example.ecommerce.dto.order.OrderRequestDTO;
import com.example.ecommerce.dto.order.OrderResponseDTO;
import com.example.ecommerce.dto.order.OrderUpdateDTO;
import com.example.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order with order items. Requires valid user ID and product IDs. Quantity must be > 0, price and total must be >= 0."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - User or Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO requestDTO) {
        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its ID including all order items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO responseDTO = orderService.getOrderById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(
            summary = "Get all orders",
            description = "Retrieves a list of all orders including their items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get orders by user ID",
            description = "Retrieves all orders for a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order by ID",
            description = "Updates an existing order. Can update status, total, and items. Validation rules apply."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data or validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Order or Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDTO updateDTO) {
        OrderResponseDTO responseDTO = orderService.updateOrder(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete order by ID",
            description = "Deletes an order and all its items from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.OrderItemDTO;
import com.example.ecommerce.dto.order.OrderRequestDTO;
import com.example.ecommerce.dto.order.OrderResponseDTO;
import com.example.ecommerce.dto.order.OrderUpdateDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setTotal(requestDTO.getTotal());
        order.setStatus(requestDTO.getStatus());

        for (OrderItemDTO itemDTO : requestDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getPrice());
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderUpdateDTO updateDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (updateDTO.getTotal() != null) {
            order.setTotal(updateDTO.getTotal());
        }

        if (updateDTO.getStatus() != null) {
            order.setStatus(updateDTO.getStatus());
        }

        if (updateDTO.getItems() != null) {
            order.getItems().clear();

            for (OrderItemDTO itemDTO : updateDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setPrice(itemDTO.getPrice());
                order.addItem(orderItem);
            }
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTOs.add(itemDTO);
        }
        dto.setItems(itemDTOs);

        return dto;
    }
}

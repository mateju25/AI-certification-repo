package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.ProductRequestDTO;
import com.example.ecommerce.dto.product.ProductResponseDTO;
import com.example.ecommerce.dto.product.ProductUpdateDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setStock(requestDTO.getStock());

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO updateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (updateDTO.getName() != null) {
            product.setName(updateDTO.getName());
        }

        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }

        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }

        if (updateDTO.getStock() != null) {
            product.setStock(updateDTO.getStock());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}

package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ErrorResponseDTO;
import com.example.ecommerce.dto.product.ProductRequestDTO;
import com.example.ecommerce.dto.product.ProductResponseDTO;
import com.example.ecommerce.dto.product.ProductUpdateDTO;
import com.example.ecommerce.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product with the provided information. Price and stock must be >= 0."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
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
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO responseDTO = productService.createProduct(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a specific product by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO responseDTO = productService.getProductById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
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
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update product by ID",
            description = "Updates an existing product's information. Price and stock must be >= 0 if provided."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        ProductResponseDTO responseDTO = productService.updateProduct(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product by ID",
            description = "Deletes a product from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

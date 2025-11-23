package com.example.ecommerce.integration;

import com.example.ecommerce.dto.auth.LoginRequestDTO;
import com.example.ecommerce.dto.auth.LoginResponseDTO;
import com.example.ecommerce.dto.order.OrderItemDTO;
import com.example.ecommerce.dto.order.OrderRequestDTO;
import com.example.ecommerce.dto.order.OrderResponseDTO;
import com.example.ecommerce.dto.order.OrderUpdateDTO;
import com.example.ecommerce.dto.product.ProductRequestDTO;
import com.example.ecommerce.dto.product.ProductResponseDTO;
import com.example.ecommerce.dto.product.ProductUpdateDTO;
import com.example.ecommerce.dto.user.UserRequestDTO;
import com.example.ecommerce.dto.user.UserResponseDTO;
import com.example.ecommerce.dto.user.UserUpdateDTO;
import com.example.ecommerce.entity.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EcommerceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private Long userId;
    private Long productId;
    private Long orderId;

    @BeforeAll
    void setup() throws Exception {
        // Login with seeded admin user to get JWT token
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("admin@ecommerce.com");
        loginRequest.setPassword("admin123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        LoginResponseDTO loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                LoginResponseDTO.class
        );
        jwtToken = loginResponse.getToken();
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Create User - Registration")
    void testCreateUser() throws Exception {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setName("Integration Test User");
        userRequest.setEmail("integration@test.com");
        userRequest.setPassword("securePassword123");

        // Test without authentication - should fail
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized());

        // Create user with JWT token
        MvcResult userResult = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        UserResponseDTO userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(),
                UserResponseDTO.class
        );
        userId = userResponse.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Login Authentication - Success")
    void testLoginSuccess() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("securePassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("integration@test.com"));
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Authorization Failure - Invalid Credentials")
    void testAuthorizationFailure() throws Exception {
        // Test 1: Invalid credentials
        LoginRequestDTO invalidLogin = new LoginRequestDTO();
        invalidLogin.setEmail("wrong@test.com");
        invalidLogin.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        // Test 2: Missing JWT token for protected endpoint
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        // Test 3: Invalid JWT token
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Product CRUD Operations")
    void testProductCrudOperations() throws Exception {
        // CREATE Product
        ProductRequestDTO productRequest = new ProductRequestDTO();
        productRequest.setName("Test Product");
        productRequest.setDescription("A test product description");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setStock(50);

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stock").value(50))
                .andReturn();

        ProductResponseDTO productResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductResponseDTO.class
        );
        productId = productResponse.getId();

        // READ Product by ID
        mockMvc.perform(get("/api/products/" + productId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Product"));

        // READ All Products
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // UPDATE Product
        ProductUpdateDTO updateRequest = new ProductUpdateDTO();
        updateRequest.setName("Updated Product Name");
        updateRequest.setPrice(new BigDecimal("149.99"));
        updateRequest.setStock(75);

        mockMvc.perform(put("/api/products/" + productId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product Name"))
                .andExpect(jsonPath("$.price").value(149.99))
                .andExpect(jsonPath("$.stock").value(75));

        // Test validation error - negative price
        ProductRequestDTO invalidProduct = new ProductRequestDTO();
        invalidProduct.setName("Invalid Product");
        invalidProduct.setPrice(new BigDecimal("-10.00"));
        invalidProduct.setStock(10);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Order Creation and Retrieval")
    void testOrderCreationAndRetrieval() throws Exception {
        // CREATE Order
        OrderItemDTO orderItem = new OrderItemDTO();
        orderItem.setProductId(productId);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("149.99"));

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(userId);
        orderRequest.setStatus(OrderStatus.PENDING);
        orderRequest.setTotal(new BigDecimal("299.98"));
        orderRequest.setItems(Collections.singletonList(orderItem));

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total").value(299.98))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andReturn();

        OrderResponseDTO orderResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderResponseDTO.class
        );
        orderId = orderResponse.getId();

        // READ Order by ID
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items", hasSize(1)));

        // READ All Orders
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // READ Orders by User
        mockMvc.perform(get("/api/orders/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].userId").value(userId));

        // Test validation error - invalid product
        OrderItemDTO invalidItem = new OrderItemDTO();
        invalidItem.setProductId(99999L);
        invalidItem.setQuantity(1);
        invalidItem.setPrice(new BigDecimal("10.00"));

        OrderRequestDTO invalidOrder = new OrderRequestDTO();
        invalidOrder.setUserId(userId);
        invalidOrder.setStatus(OrderStatus.PENDING);
        invalidOrder.setTotal(new BigDecimal("10.00"));
        invalidOrder.setItems(Collections.singletonList(invalidItem));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: User Management Endpoints")
    void testUserManagementEndpoints() throws Exception {
        // READ User by ID
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.name").value("Integration Test User"));

        // READ All Users
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // UPDATE User
        UserUpdateDTO updateRequest = new UserUpdateDTO();
        updateRequest.setName("Updated Integration User");
        updateRequest.setEmail("updated.integration@test.com");

        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Integration User"))
                .andExpect(jsonPath("$.email").value("updated.integration@test.com"));

        // Test duplicate email error
        UserRequestDTO duplicateUser = new UserRequestDTO();
        duplicateUser.setName("Duplicate User");
        duplicateUser.setEmail("updated.integration@test.com");
        duplicateUser.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Email already exists")));

        // Test not found error
        mockMvc.perform(get("/api/users/99999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Order Status Updates and Deletion")
    void testOrderStatusUpdatesAndDeletion() throws Exception {
        // UPDATE Order Status
        OrderUpdateDTO updateRequest = new OrderUpdateDTO();
        updateRequest.setStatus(OrderStatus.PROCESSING);
        updateRequest.setTotal(new BigDecimal("299.98"));

        mockMvc.perform(put("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // Update to COMPLETED
        updateRequest.setStatus(OrderStatus.COMPLETED);
        mockMvc.perform(put("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Update to EXPIRED
        updateRequest.setStatus(OrderStatus.EXPIRED);
        mockMvc.perform(put("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));

        // DELETE Order
        mockMvc.perform(delete("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        // DELETE Product
        mockMvc.perform(delete("/api/products/" + productId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify product deletion
        mockMvc.perform(get("/api/products/" + productId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        // DELETE User
        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify user deletion
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}

# PRP: Kafka Event-Driven Order Processing System

## Goal
Implement an event-driven order processing system using Apache Kafka for asynchronous order handling, payment simulation, automatic expiration, and notifications with complete audit trail.

## Why
- **Decoupling**: Separates order creation from payment processing and notifications
- **Scalability**: Enables asynchronous processing for better system performance
- **Reliability**: Provides event replay capability and guaranteed message delivery
- **Audit Trail**: Maintains complete history of all order events and notifications
- **Business Logic**: Simulates real-world payment processing with 50% success/failure rate
- **Automation**: Auto-expires stale processing orders to prevent indefinite pending states

## What
Implement a complete event-driven architecture that:
1. Publishes `OrderCreated` event when orders are created via POST /api/orders
2. Asynchronously processes orders (PENDING → PROCESSING, simulates payment, 50% → COMPLETED)
3. Runs scheduled job every 60 seconds to expire PROCESSING orders older than 10 minutes
4. Publishes `OrderCompleted` and `OrderExpired` events
5. Handles events to send notifications (email simulation + database persistence)
6. Integrates Kafka via Docker Compose alongside existing PostgreSQL

### Success Criteria
- [x] Kafka and Zookeeper running in Docker Compose
- [x] OrderCreated event published on order creation (POST /api/orders)
- [x] Event consumer updates status PENDING → PROCESSING
- [x] Payment simulation with 5-second delay
- [x] 50% of orders transition to COMPLETED with OrderCompleted event
- [x] Scheduled job expires PROCESSING orders older than 10 minutes
- [x] OrderExpired event published for expired orders
- [x] Email notifications logged to console for COMPLETED orders
- [x] Notifications table stores all events (COMPLETED and EXPIRED)
- [x] Integration tests verify Kafka event flow (minimum 4 tests)
- [x] All existing tests still pass

## All Needed Context

### Documentation & References

```yaml
# CRITICAL - Read these files in your context first
- file: C:\Work\Repos\ai-assignment\CLAUDE.md
  why: Complete project conventions, error handling, testing patterns, order status enum values
  critical: |
    - Order Status enum values: PENDING, PROCESSING, COMPLETED, EXPIRED (no SHIPPED/DELIVERED)
    - Error response structure uses ErrorMessageDTO/ValidationErrorDTO (no status field)
    - Admin credentials: admin@ecommerce.com / admin123
    - Integration test pattern: PER_CLASS lifecycle, BeforeAll JWT setup
    - Validation gates: mvn test (all tests must pass)

- file: C:\Work\Repos\ai-assignment\README.md
  why: Technology stack (Spring Boot 3.2.1, Java 17), project structure, API endpoints

- file: C:\Work\Repos\ai-assignment\API_DOCUMENTATION.md
  why: Complete error response patterns and status codes

- file: C:\Work\Repos\ai-assignment\src\main\java\com\example\ecommerce\service\OrderService.java
  why: MUST inject EventPublisher here to publish OrderCreated event in createOrder() method
  pattern: |
    Follow existing pattern:
    - Constructor injection with @RequiredArgsConstructor
    - Call eventPublisher.publishOrderCreated(order) AFTER orderRepository.save()

- file: C:\Work\Repos\ai-assignment\src\main\java\com\example\ecommerce\entity\Order.java
  why: Entity pattern with Lombok, JPA annotations, timestamps
  pattern: Uses @CreationTimestamp and @UpdateTimestamp from Hibernate

- file: C:\Work\Repos\ai-assignment\src\main\java\com\example\ecommerce\config\DataSeeder.java
  why: CommandLineRunner pattern for initialization
  pattern: Use @Component, @RequiredArgsConstructor, @Slf4j for consistency

- file: C:\Work\Repos\ai-assignment\src\test\java\com\example\ecommerce\integration\EcommerceIntegrationTest.java
  why: Integration test pattern - MUST follow exactly
  pattern: |
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)

# MUST READ - External documentation with specific sections
- url: https://www.baeldung.com/spring-kafka
  section: "Configuring Topics, Producing Messages, Consuming Messages"
  why: Official Spring Kafka integration patterns with KafkaTemplate and @KafkaListener
  critical: |
    - KafkaTemplate<String, Object> for sending events
    - @KafkaListener(topics = "topic-name", groupId = "group-id")
    - JSON serialization: org.springframework.kafka.support.serializer.JsonSerializer
    - ConsumerFactory and ProducerFactory configuration

- url: https://www.baeldung.com/ops/kafka-docker-setup
  section: "Docker Compose Configuration"
  why: Kafka + Zookeeper Docker setup with correct environment variables
  critical: |
    - Use Confluent images: confluentinc/cp-zookeeper and confluentinc/cp-kafka
    - Zookeeper on port 2181, Kafka on 9092 (internal) and 29092 (external)
    - KAFKA_ADVERTISED_LISTENERS critical for Docker networking

- url: https://www.baeldung.com/spring-scheduled-tasks
  section: "@Scheduled annotation and @EnableScheduling"
  why: Scheduled job configuration for order expiration
  critical: |
    - @EnableScheduling on main application class
    - @Scheduled(fixedRate = 60000) for 60-second interval
    - Methods must return void and accept no arguments
    - Use fixedRate (not fixedDelay) for consistent intervals

- url: https://docs.spring.io/spring-kafka/reference/kafka/serdes.html
  section: "JSON Serialization Configuration"
  why: Proper JSON serialization setup for event objects
  critical: |
    - spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
    - spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
    - spring.kafka.consumer.properties.spring.json.trusted.packages=* (for testing)

- url: https://howtodoinjava.com/kafka/spring-boot-kafkalistener-kafkahandler-example/
  section: "@KafkaListener configuration and error handling"
  why: Consumer implementation patterns with proper exception handling
```

### Current Codebase Structure

```
src/main/java/com/example/ecommerce/
├── EcommerceApiApplication.java          # Main Spring Boot app
├── config/
│   ├── DataSeeder.java                   # CommandLineRunner for admin user
│   ├── OpenApiConfig.java                # Swagger configuration
│   └── SecurityConfig.java               # JWT security setup
├── controller/
│   ├── AuthenticationController.java     # POST /api/auth/login
│   ├── OrderController.java              # Order CRUD endpoints
│   ├── ProductController.java
│   └── UserController.java
├── dto/
│   ├── auth/                             # LoginRequestDTO, LoginResponseDTO
│   ├── order/                            # OrderRequestDTO, OrderResponseDTO, etc.
│   ├── product/
│   ├── user/
│   ├── ErrorMessageDTO.java              # Simple error responses
│   └── ValidationErrorDTO.java           # Validation error responses
├── entity/
│   ├── Order.java                        # JPA entity with items, status, timestamps
│   ├── OrderItem.java
│   ├── OrderStatus.java                  # enum: PENDING, PROCESSING, COMPLETED, EXPIRED
│   ├── Product.java
│   └── User.java
├── exception/
│   ├── GlobalExceptionHandler.java       # @RestControllerAdvice for error handling
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── UnauthorizedException.java
├── repository/
│   ├── OrderRepository.java              # JpaRepository<Order, Long>
│   ├── OrderItemRepository.java
│   ├── ProductRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtUtil.java                      # JWT token generation/validation
│   ├── JwtAuthenticationFilter.java      # Request filter for JWT
│   ├── CustomUserDetailsService.java
│   └── CustomAuthenticationEntryPoint.java
└── service/
    ├── OrderService.java                 # Business logic for orders
    ├── ProductService.java
    ├── UserService.java
    └── AuthenticationService.java

src/test/java/com/example/ecommerce/
└── integration/
    └── EcommerceIntegrationTest.java     # Complete integration test suite

docker-compose.yml                        # PostgreSQL only (need to add Kafka)
pom.xml                                   # Maven dependencies
```

### Desired Codebase Structure (New Files)

```
src/main/java/com/example/ecommerce/
├── EcommerceApiApplication.java          # MODIFY: Add @EnableScheduling
├── config/
│   ├── KafkaConfig.java                  # NEW: Kafka producer/consumer configuration
│   └── KafkaTopicConfig.java             # NEW: Topic definitions (order-events)
├── event/
│   ├── OrderCreatedEvent.java            # NEW: Event DTO with orderId, userId, total, timestamp
│   ├── OrderCompletedEvent.java          # NEW: Event DTO with orderId, timestamp
│   └── OrderExpiredEvent.java            # NEW: Event DTO with orderId, timestamp
├── entity/
│   └── Notification.java                 # NEW: JPA entity (id, orderId, type, message, createdAt)
├── repository/
│   └── NotificationRepository.java       # NEW: JpaRepository<Notification, Long>
├── service/
│   ├── OrderService.java                 # MODIFY: Inject EventPublisher, publish OrderCreated
│   ├── OrderEventPublisher.java          # NEW: Publishes events to Kafka using KafkaTemplate
│   ├── OrderEventConsumer.java           # NEW: @KafkaListener for OrderCreated events
│   ├── NotificationService.java          # NEW: Handles OrderCompleted/Expired notifications
│   └── OrderExpirationScheduler.java     # NEW: @Scheduled job for expiring old orders
└── service/

src/test/java/com/example/ecommerce/
└── integration/
    └── KafkaOrderEventIntegrationTest.java  # NEW: Kafka event flow tests (4+ tests)

docker-compose.yml                        # MODIFY: Add Zookeeper and Kafka services
pom.xml                                   # MODIFY: Add spring-kafka dependency
src/main/resources/application.properties # MODIFY: Add Kafka configuration
src/test/resources/application-test.properties # MODIFY: Add Kafka test configuration
```

### Known Gotchas & Critical Details

```java
// CRITICAL: Order Status Enum Values (from CLAUDE.md)
// ONLY these 4 values exist - NO SHIPPED or DELIVERED
public enum OrderStatus {
    PENDING,      // Initial state when order created
    PROCESSING,   // After OrderCreated event consumed
    COMPLETED,    // After successful payment (50% chance)
    EXPIRED       // After 10 minutes in PROCESSING state
}

// CRITICAL: Error Response Structure (from CLAUDE.md)
// NO "status", "error", or "timestamp" fields in actual responses
// Only "message" for ErrorMessageDTO, "message" + "validationErrors" for ValidationErrorDTO
// Tests MUST NOT check for $.status field - check HTTP status code directly

// CRITICAL: Kafka Configuration
// Spring Boot 3.2.1 uses spring-kafka 3.x which has breaking changes from 2.x
// JSON serialization requires trusted packages configuration in consumer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.ecommerce.event,java.lang

// CRITICAL: @KafkaListener error handling
// By default, Kafka listeners are synchronous and block until complete
// Exceptions in listeners cause message redelivery (can create infinite loop)
// MUST wrap processing in try-catch and log errors

// CRITICAL: @Scheduled job timing
// fixedRate = 60000 means job runs every 60 seconds REGARDLESS of execution time
// If job takes > 60 seconds, multiple instances run concurrently
// Use @Transactional to prevent race conditions when updating orders

// CRITICAL: Timestamp comparison for expiration
// Order.updatedAt is set automatically by @UpdateTimestamp
// For expiration check: updatedAt < (now - 10 minutes) AND status = PROCESSING
// Must use LocalDateTime.now().minusMinutes(10) for comparison

// CRITICAL: Kafka in Docker networking
// Application connects to Kafka at localhost:29092 (from host)
// Kafka containers communicate via kafka:9092 (internal Docker network)
// KAFKA_ADVERTISED_LISTENERS MUST include both listeners

// CRITICAL: Event Publishing Timing
// MUST publish OrderCreated AFTER Order is persisted (has ID)
// If you publish before save(), orderId will be null
// Pattern: Order saved = orderRepository.save(order); eventPublisher.publish(saved);

// CRITICAL: Payment Simulation (50% success rate)
// Use Random or Math.random() to determine success
// boolean success = Math.random() < 0.5;
// Thread.sleep(5000) to simulate processing delay
// ONLY update to COMPLETED if success, otherwise leave as PROCESSING

// CRITICAL: Notification Types
// Use String enum or constant for notification types
// "ORDER_COMPLETED" for completed orders (fake email + DB save)
// "ORDER_EXPIRED" for expired orders (DB save only, no email)

// CRITICAL: Spring Boot Test with Kafka
// Kafka may not be available in test profile
// Either use @EmbeddedKafka or @Disabled for Kafka tests in CI
// Or configure separate Kafka for tests in application-test.properties
```

## Implementation Blueprint

### Data Models and Structure

```java
// NEW: Event DTOs (in com.example.ecommerce.event package)
// Use these as Kafka message payloads

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal total;
    private LocalDateTime timestamp;
    // No need for @Entity - these are just DTOs for Kafka
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompletedEvent {
    private Long orderId;
    private LocalDateTime timestamp;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderExpiredEvent {
    private Long orderId;
    private LocalDateTime timestamp;
}

// NEW: Notification Entity (in com.example.ecommerce.entity package)
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 50)
    private String type;  // "ORDER_COMPLETED" or "ORDER_EXPIRED"

    @Column(nullable = false, length = 500)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

// NEW: NotificationRepository (in repository package)
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderId(Long orderId);
}
```

### Task List (In Order of Implementation)

```yaml
Task 1: Update Docker Compose with Kafka and Zookeeper
  MODIFY: docker-compose.yml
  ACTION: |
    - ADD Zookeeper service (confluentinc/cp-zookeeper:latest)
      - port 2181:2181
      - ZOOKEEPER_CLIENT_PORT=2181
      - ZOOKEEPER_TICK_TIME=2000
    - ADD Kafka service (confluentinc/cp-kafka:latest)
      - port 29092:29092
      - depends_on: zookeeper
      - KAFKA_BROKER_ID=1
      - KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
      - KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      - KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      - KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
  VERIFY: docker-compose up -d && docker ps | grep kafka

Task 2: Add Spring Kafka Dependency
  MODIFY: pom.xml
  ACTION: |
    ADD inside <dependencies>:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
  VERIFY: mvn clean compile (should download spring-kafka)

Task 3: Configure Kafka Properties
  MODIFY: src/main/resources/application.properties
  ACTION: |
    ADD at end of file:
    # Kafka Configuration
    spring.kafka.bootstrap-servers=localhost:29092
    spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
    spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
    spring.kafka.consumer.group-id=order-service-group
    spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
    spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
    spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.ecommerce.event
    spring.kafka.consumer.auto-offset-reset=earliest

  MODIFY: src/test/resources/application-test.properties
  ACTION: |
    ADD at end:
    # Kafka Test Configuration (use embedded or mock)
    spring.kafka.bootstrap-servers=localhost:29092
    spring.kafka.consumer.group-id=test-group
    spring.kafka.consumer.properties.spring.json.trusted.packages=*

Task 4: Create Kafka Configuration Classes
  CREATE: src/main/java/com/example/ecommerce/config/KafkaTopicConfig.java
  PATTERN: Mirror DataSeeder.java structure
  ACTION: |
    - @Configuration class
    - Define @Bean for topic "order-events"
    - Use TopicBuilder.name("order-events").partitions(1).replicas(1).build()

  CREATE: src/main/java/com/example/ecommerce/config/KafkaConfig.java (OPTIONAL)
  WHY: Spring Boot auto-configures Kafka from properties, but create this for custom settings
  ACTION: |
    - @Configuration
    - Can define custom ProducerFactory, ConsumerFactory if needed
    - For now, rely on Spring Boot autoconfiguration

Task 5: Create Event DTOs
  CREATE: src/main/java/com/example/ecommerce/event/OrderCreatedEvent.java
  CREATE: src/main/java/com/example/ecommerce/event/OrderCompletedEvent.java
  CREATE: src/main/java/com/example/ecommerce/event/OrderExpiredEvent.java
  PATTERN: Use @Data, @NoArgsConstructor, @AllArgsConstructor like existing DTOs
  ACTION: See "Data Models and Structure" section above for field details
  VERIFY: mvn compile (should compile without errors)

Task 6: Create Notification Entity and Repository
  CREATE: src/main/java/com/example/ecommerce/entity/Notification.java
  PATTERN: Mirror Order.java entity structure
  ACTION: See "Data Models and Structure" section above

  CREATE: src/main/java/com/example/ecommerce/repository/NotificationRepository.java
  PATTERN: Exactly like OrderRepository.java - extends JpaRepository
  VERIFY: mvn compile && check H2/PostgreSQL tables created on startup

Task 7: Create Event Publisher Service
  CREATE: src/main/java/com/example/ecommerce/service/OrderEventPublisher.java
  PATTERN: Mirror service pattern (see pseudocode below)
  ACTION: |
    - @Service with @RequiredArgsConstructor
    - Inject KafkaTemplate<String, Object>
    - Method: publishOrderCreated(Order order)
    - Method: publishOrderCompleted(Long orderId)
    - Method: publishOrderExpired(Long orderId)
    - Use kafkaTemplate.send("order-events", event)

Task 8: Modify OrderService to Publish Events
  MODIFY: src/main/java/com/example/ecommerce/service/OrderService.java
  ACTION: |
    - INJECT OrderEventPublisher in constructor
    - FIND createOrder() method
    - AFTER line: Order savedOrder = orderRepository.save(order);
    - ADD: orderEventPublisher.publishOrderCreated(savedOrder);
    - DO NOT change any other logic
  VERIFY: POST /api/orders should still work, check logs for "Published OrderCreatedEvent"

Task 9: Create Order Event Consumer
  CREATE: src/main/java/com/example/ecommerce/service/OrderEventConsumer.java
  PATTERN: See pseudocode below for @KafkaListener
  ACTION: |
    - @Service with @RequiredArgsConstructor, @Slf4j
    - Inject OrderRepository, OrderEventPublisher
    - @KafkaListener(topics = "order-events", groupId = "order-processor-group")
    - Method: handleOrderCreated(OrderCreatedEvent event)
    - Wrap in try-catch for error handling
    - Load order, update to PROCESSING, save
    - Sleep 5 seconds (payment simulation)
    - Random 50% success: update to COMPLETED or leave PROCESSING
    - If COMPLETED: publish OrderCompletedEvent

Task 10: Create Notification Service
  CREATE: src/main/java/com/example/ecommerce/service/NotificationService.java
  ACTION: |
    - @Service with @RequiredArgsConstructor, @Slf4j
    - Inject NotificationRepository
    - Two @KafkaListener methods (different groupId for notifications)
    - handleOrderCompleted(OrderCompletedEvent): log fake email + save to DB
    - handleOrderExpired(OrderExpiredEvent): save to DB only

Task 11: Create Order Expiration Scheduler
  CREATE: src/main/java/com/example/ecommerce/service/OrderExpirationScheduler.java
  PATTERN: @Component with @Scheduled annotation
  ACTION: |
    - @Component, @RequiredArgsConstructor, @Slf4j
    - Inject OrderRepository, OrderEventPublisher
    - @Scheduled(fixedRate = 60000) // 60 seconds
    - @Transactional method: expireOldOrders()
    - Query: findByStatusAndUpdatedAtBefore(PROCESSING, 10 minutes ago)
    - For each: update to EXPIRED, publish OrderExpiredEvent

  MODIFY: src/main/java/com/example/ecommerce/EcommerceApiApplication.java
  ACTION: |
    - ADD @EnableScheduling annotation to class

  MODIFY: src/main/java/com/example/ecommerce/repository/OrderRepository.java
  ACTION: |
    - ADD method: List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime dateTime);

Task 12: Create Integration Tests for Kafka Flow
  CREATE: src/test/java/com/example/ecommerce/integration/KafkaOrderEventIntegrationTest.java
  PATTERN: Exactly like EcommerceIntegrationTest.java
  ACTION: |
    - Same test setup annotations
    - @BeforeAll: login and get JWT token
    - Test 1: Create order, verify OrderCreated event processing (status becomes PROCESSING)
    - Test 2: Verify 50% orders eventually become COMPLETED (may need retry/await)
    - Test 3: Create order, wait 11 minutes (mocked), verify expiration job marks as EXPIRED
    - Test 4: Verify notifications table has entries for COMPLETED and EXPIRED orders
    - Use @Autowired CountDownLatch or Awaitility for async verification

Task 13: Run All Tests and Verify
  ACTION: |
    - mvn clean test
    - All existing tests must pass
    - New Kafka tests must pass
    - Check for compilation errors
  VERIFY: Build success, 0 failures
```

### Pseudocode for Key Components

```java
// Task 7: OrderEventPublisher.java
package com.example.ecommerce.service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderCreated(Order order) {
        // PATTERN: Create event DTO from entity
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getUser().getId(),
            order.getTotal(),
            LocalDateTime.now()
        );

        // CRITICAL: Use kafkaTemplate.send() - returns CompletableFuture
        kafkaTemplate.send(TOPIC, "order-created-" + order.getId(), event)
            .thenAccept(result -> log.info("Published OrderCreatedEvent for order: {}", order.getId()))
            .exceptionally(ex -> {
                log.error("Failed to publish OrderCreatedEvent", ex);
                return null;
            });
    }

    public void publishOrderCompleted(Long orderId) {
        OrderCompletedEvent event = new OrderCompletedEvent(orderId, LocalDateTime.now());
        kafkaTemplate.send(TOPIC, "order-completed-" + orderId, event)
            .thenAccept(result -> log.info("Published OrderCompletedEvent for order: {}", orderId))
            .exceptionally(ex -> {
                log.error("Failed to publish OrderCompletedEvent", ex);
                return null;
            });
    }

    public void publishOrderExpired(Long orderId) {
        OrderExpiredEvent event = new OrderExpiredEvent(orderId, LocalDateTime.now());
        kafkaTemplate.send(TOPIC, "order-expired-" + orderId, event)
            .thenAccept(result -> log.info("Published OrderExpiredEvent for order: {}", orderId))
            .exceptionally(ex -> {
                log.error("Failed to publish OrderExpiredEvent", ex);
                return null;
            });
    }
}

// Task 9: OrderEventConsumer.java
package com.example.ecommerce.service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    // CRITICAL: @KafkaListener processes events asynchronously
    // MUST use different groupId than NotificationService to ensure both receive events
    @KafkaListener(topics = "order-events", groupId = "order-processor-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received OrderCreatedEvent for order: {}", event.getOrderId());

            // PATTERN: Load entity from repository
            Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + event.getOrderId()));

            // Update status to PROCESSING
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
            log.info("Order {} status updated to PROCESSING", order.getId());

            // CRITICAL: Simulate payment processing (5 second delay)
            Thread.sleep(5000);

            // CRITICAL: 50% success rate for payment
            boolean paymentSuccess = Math.random() < 0.5;

            if (paymentSuccess) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                log.info("Order {} payment successful - status updated to COMPLETED", order.getId());

                // Publish OrderCompleted event
                eventPublisher.publishOrderCompleted(order.getId());
            } else {
                log.info("Order {} payment failed - remains in PROCESSING", order.getId());
                // Order stays in PROCESSING - will be expired by scheduler if not retried
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for order: {}", event.getOrderId(), e);
        } catch (Exception e) {
            // CRITICAL: Catch all exceptions to prevent infinite retry loop
            log.error("Error processing OrderCreatedEvent for order: {}", event.getOrderId(), e);
        }
    }
}

// Task 10: NotificationService.java
package com.example.ecommerce.service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // CRITICAL: Use different groupId to receive same events as OrderEventConsumer
    @KafkaListener(topics = "order-events", groupId = "notification-service-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            log.info("Received OrderCompletedEvent for order: {}", event.getOrderId());

            // PATTERN: Simulate email notification (log to console)
            String emailMessage = String.format(
                "FAKE EMAIL: Order %d has been completed successfully!",
                event.getOrderId()
            );
            log.info("📧 {}", emailMessage);

            // PATTERN: Save notification to database (audit trail)
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

    @KafkaListener(topics = "order-events", groupId = "notification-service-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderExpired(OrderExpiredEvent event) {
        try {
            log.info("Received OrderExpiredEvent for order: {}", event.getOrderId());

            // PATTERN: No email for expired orders, just save to database
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

// Task 11: OrderExpirationScheduler.java
package com.example.ecommerce.service;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationScheduler {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    // CRITICAL: fixedRate = 60000 means every 60 seconds
    // Runs regardless of previous execution completion
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOldOrders() {
        try {
            // PATTERN: Find PROCESSING orders older than 10 minutes
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

            // PATTERN: Update each order and publish event
            for (Order order : expiredOrders) {
                order.setStatus(OrderStatus.EXPIRED);
                orderRepository.save(order);

                // Publish OrderExpired event
                eventPublisher.publishOrderExpired(order.getId());

                log.info("Order {} expired (was in PROCESSING for > 10 minutes)", order.getId());
            }

        } catch (Exception e) {
            // CRITICAL: Log but don't throw - let scheduler continue
            log.error("Error in expireOldOrders scheduled job", e);
        }
    }
}
```

### Integration Points

```yaml
DATABASE:
  - migration: "JPA auto-creates notifications table on startup (ddl-auto=update)"
  - table: notifications (id BIGINT, order_id BIGINT, type VARCHAR(50), message VARCHAR(500), created_at TIMESTAMP)
  - index: "Consider adding INDEX on order_id if querying by order frequently"

DOCKER:
  - add to: docker-compose.yml
  - services: zookeeper (port 2181), kafka (ports 9092, 29092)
  - dependencies: kafka depends_on zookeeper, app should depend_on kafka (optional)

CONFIG:
  - add to: src/main/resources/application.properties
  - pattern: "spring.kafka.* properties for producer/consumer"
  - add to: src/test/resources/application-test.properties
  - pattern: "Same Kafka config for tests (or use @EmbeddedKafka)"

DEPENDENCIES:
  - add to: pom.xml
  - dependency: spring-boot-starter-kafka (Spring manages version via BOM)

MAIN APPLICATION:
  - modify: EcommerceApiApplication.java
  - add: @EnableScheduling annotation

REPOSITORY:
  - modify: OrderRepository.java
  - add: List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime dateTime);
  - pattern: Spring Data JPA derives query from method name

SERVICE:
  - modify: OrderService.java
  - inject: OrderEventPublisher
  - modify: createOrder() method to call eventPublisher.publishOrderCreated(savedOrder)
```

## Validation Loop

### Level 0: Docker Infrastructure
```bash
# Start Docker services
docker-compose up -d

# Verify Zookeeper is running
docker ps | grep zookeeper
# Expected: Container running on port 2181

# Verify Kafka is running
docker ps | grep kafka
# Expected: Container running on ports 9092, 29092

# Check Kafka logs for successful startup
docker-compose logs kafka | grep "started"
# Expected: "Kafka Server started" message

# Verify Kafka topic creation (after app starts once)
docker exec -it <kafka-container-id> kafka-topics --list --bootstrap-server localhost:9092
# Expected: "order-events" topic listed
```

### Level 1: Compilation & Build
```bash
# Clean and compile
mvn clean compile

# Expected: BUILD SUCCESS with no errors
# If errors: Read compilation errors, fix issues, re-run

# Build with tests disabled to verify code compiles
mvn clean install -DskipTests

# Expected: BUILD SUCCESS
```

### Level 2: Unit Tests (Existing)
```bash
# Run all existing tests to ensure no regression
mvn test

# Expected: All existing tests pass (EcommerceIntegrationTest)
# If failures: Check if OrderService changes broke tests
# Common issue: EventPublisher not mocked - add @MockBean in tests if needed
```

### Level 3: Integration Tests (Kafka Flow)
```bash
# Run new Kafka integration tests
mvn test -Dtest=KafkaOrderEventIntegrationTest

# Expected: All 4+ Kafka tests pass
# Test 1: Order created → status becomes PROCESSING
# Test 2: ~50% of orders become COMPLETED
# Test 3: Orders expire after 10 minutes in PROCESSING
# Test 4: Notifications table has entries

# If failing:
# - Check Kafka connection (localhost:29092 reachable?)
# - Check event serialization (JSON format correct?)
# - Check consumer group IDs (distinct for processor vs notifications?)
# - Use @Await or Thread.sleep() for async event verification
```

### Level 4: Manual End-to-End Test
```bash
# 1. Start application
mvn spring-boot:run

# 2. Get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecommerce.com","password":"admin123"}'
# Copy the token

# 3. Create a test product (if needed)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "For Kafka test",
    "price": 99.99,
    "stock": 100
  }'
# Note the product ID

# 4. Create a test user (if needed)
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@test.com",
    "password": "password123"
  }'
# Note the user ID

# 5. Create an order (triggers event flow)
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "total": 199.98,
    "status": "PENDING",
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 99.99
      }
    ]
  }'
# Expected: 201 Created with order details
# Note the order ID

# 6. Check application logs
# Expected logs (within 5-10 seconds):
# - "Published OrderCreatedEvent for order: X"
# - "Received OrderCreatedEvent for order: X"
# - "Order X status updated to PROCESSING"
# - "Order X payment successful - status updated to COMPLETED" (50% chance)
#   OR "Order X payment failed - remains in PROCESSING"
# - If completed: "Published OrderCompletedEvent for order: X"
# - If completed: "📧 FAKE EMAIL: Order X has been completed successfully!"
# - If completed: "Notification saved to database for order: X"

# 7. Verify order status changed
curl -X GET http://localhost:8080/api/orders/<order-id> \
  -H "Authorization: Bearer <token>"
# Expected: status should be "PROCESSING" or "COMPLETED" (not "PENDING")

# 8. Check notifications table (if order completed)
# Access H2 console: http://localhost:8080/h2-console
# Query: SELECT * FROM notifications WHERE order_id = <order-id>
# Expected: One row with type "ORDER_COMPLETED"

# 9. Test expiration (create order that stays in PROCESSING)
# Repeat step 5 multiple times until one stays in PROCESSING (50% chance)
# Wait 11 minutes (or modify code to use 1 minute for testing)
# Check logs for: "Order X expired (was in PROCESSING for > 10 minutes)"
# Verify: Order status becomes EXPIRED
# Verify: Notification table has entry with type "ORDER_EXPIRED"
```

## Final Validation Checklist

- [ ] Docker Compose starts successfully: `docker-compose up -d`
- [ ] Kafka and Zookeeper containers running: `docker ps`
- [ ] Application starts without errors: `mvn spring-boot:run`
- [ ] All compilation successful: `mvn clean compile`
- [ ] All existing tests pass: `mvn test`
- [ ] New Kafka integration tests pass (minimum 4 tests)
- [ ] POST /api/orders creates order with status PENDING
- [ ] OrderCreatedEvent published to Kafka (check logs)
- [ ] Order status automatically updates to PROCESSING (async)
- [ ] ~50% of orders transition to COMPLETED after 5 seconds
- [ ] OrderCompletedEvent published for completed orders
- [ ] Fake email logged to console for completed orders
- [ ] Notification saved to database for completed orders
- [ ] Scheduled job runs every 60 seconds (check logs)
- [ ] Orders in PROCESSING > 10 minutes marked as EXPIRED
- [ ] OrderExpiredEvent published for expired orders
- [ ] Notification saved to database for expired orders
- [ ] No exceptions in logs during normal operation
- [ ] Swagger UI still accessible: http://localhost:8080/swagger-ui.html
- [ ] H2 Console shows notifications table: http://localhost:8080/h2-console

---

## Anti-Patterns to Avoid

- ❌ Don't publish events BEFORE persisting entities (orderId will be null)
- ❌ Don't use same consumer groupId for OrderEventConsumer and NotificationService
- ❌ Don't forget @EnableScheduling on main application class
- ❌ Don't use blocking calls in @KafkaListener without try-catch (causes infinite retry)
- ❌ Don't hardcode Kafka topic names - use constants or configuration
- ❌ Don't forget to update docker-compose.yml with Kafka services
- ❌ Don't use SHIPPED or DELIVERED status (only PENDING, PROCESSING, COMPLETED, EXPIRED)
- ❌ Don't check for $.status field in error responses (use HTTP status code)
- ❌ Don't skip adding spring-kafka dependency to pom.xml
- ❌ Don't forget trusted.packages configuration for JSON deserialization
- ❌ Don't use fixedDelay for expiration scheduler (use fixedRate for consistent intervals)
- ❌ Don't update order status without saving to database
- ❌ Don't forget @Transactional on expiration scheduler to prevent race conditions
- ❌ Don't create new error response formats (use existing ErrorMessageDTO pattern)
- ❌ Don't modify existing endpoints behavior (only add event publishing)

---

## PRP Confidence Score: 8.5/10

### Strengths:
- ✅ Complete context from codebase and external documentation
- ✅ Exact patterns to follow from existing code
- ✅ Detailed pseudocode for all new components
- ✅ Comprehensive validation gates at multiple levels
- ✅ Explicit anti-patterns to avoid common pitfalls
- ✅ Docker Compose configuration with exact environment variables
- ✅ Step-by-step task breakdown with verification commands

### Potential Challenges:
- ⚠️ Kafka event timing in tests may need retry logic or Awaitility
- ⚠️ 50% payment success rate may cause test flakiness (consider seeding random for deterministic tests)
- ⚠️ Thread.sleep(5000) in consumer blocks thread (acceptable for POC, consider CompletableFuture for production)
- ⚠️ Expiration test requires 10-minute wait or code modification for faster testing

### Mitigation Strategies:
1. Use `@Await` or `Awaitility.await()` in tests for async event verification
2. Mock `Math.random()` or inject `Random` with seed for deterministic test outcomes
3. Document that payment processing is synchronous by design (simple implementation)
4. Suggest adding test profile property `order.expiration.minutes=1` for faster test runs

**Recommendation**: This PRP should enable one-pass implementation with high confidence, though integration test verification may require 1-2 iterations to handle async timing correctly.

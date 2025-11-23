# E-commerce REST API

A comprehensive Spring Boot REST API for an e-commerce application with JWT authentication, validation, and complete OpenAPI/Swagger documentation.

## Features

- User management with CRUD operations
- JWT-based authentication and authorization
- Product management
- Order management with order items
- **Event-driven architecture with Apache Kafka**
- **Asynchronous order processing with payment simulation**
- **Automated order expiration for stale orders**
- **Real-time notifications system**
- Input validation with detailed error messages
- Comprehensive error handling (400, 401, 404, 500)
- **Complete OpenAPI/Swagger documentation with error codes**
- **Interactive API testing via Swagger UI**
- Fully documented error responses for all endpoints
- H2 in-memory database for development

## Technology Stack

- Java 17
- Spring Boot 3.2.1
- Spring Security with JWT
- Spring Data JPA
- Hibernate
- H2 Database (development)
- PostgreSQL (production-ready)
- Apache Kafka for event-driven architecture
- Spring for Apache Kafka
- Lombok
- Maven
- SpringDoc OpenAPI 3
- Docker & Docker Compose

## Project Structure

```
src/main/java/com/example/ecommerce/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
├── exception/           # Custom exceptions and handler
├── repository/          # JPA repositories
├── security/            # Security and JWT components
└── service/             # Business logic
```

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6+

### Running the Application

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Accessing Swagger UI

Once the application is running, access the comprehensive interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

**Swagger UI Features:**
- Complete endpoint documentation with descriptions
- Request/response schemas with examples
- **Documented error responses (400, 401, 404, 500) for each endpoint**
- Interactive "Try it out" functionality
- JWT Bearer token authentication support
- Example values for all data models

**Using JWT Authentication in Swagger:**
1. Use `/api/auth/login` endpoint to get a JWT token
2. Click the "Authorize" button at the top right
3. Enter: `Bearer <your-jwt-token>`
4. Click "Authorize" - all requests will now include authentication

For detailed error response documentation, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

### H2 Console

Access the H2 database console at:
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:ecommerce
Username: sa
Password: (leave empty)
```

## API Endpoints

### Authentication Module

#### Login
- **POST** `/api/auth/login`
- **Description**: Authenticate user and receive JWT token
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@example.com"
}
```

### Users Module

All endpoints require JWT Bearer token except login.

#### Create User
- **POST** `/api/users`
- **Description**: Create a new user
- **Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### Get User by ID
- **GET** `/api/users/{id}`
- **Description**: Retrieve user by ID

#### Get All Users
- **GET** `/api/users`
- **Description**: Retrieve all users

#### Update User
- **PUT** `/api/users/{id}`
- **Description**: Update user information
- **Request Body**:
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "newPassword123"
}
```

#### Delete User
- **DELETE** `/api/users/{id}`
- **Description**: Delete user by ID

### Products Module

All endpoints require JWT Bearer token.

#### Create Product
- **POST** `/api/products`
- **Description**: Create a new product
- **Request Body**:
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99,
  "stock": 50
}
```

#### Get Product by ID
- **GET** `/api/products/{id}`
- **Description**: Retrieve product by ID

#### Get All Products
- **GET** `/api/products`
- **Description**: Retrieve all products

#### Update Product
- **PUT** `/api/products/{id}`
- **Description**: Update product information
- **Request Body**:
```json
{
  "name": "Gaming Laptop",
  "description": "Updated description",
  "price": 1499.99,
  "stock": 45
}
```

#### Delete Product
- **DELETE** `/api/products/{id}`
- **Description**: Delete product by ID

### Orders Module

All endpoints require JWT Bearer token.

#### Create Order
- **POST** `/api/orders`
- **Description**: Create a new order
- **Request Body**:
```json
{
  "userId": 1,
  "total": 2599.98,
  "status": "PENDING",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 1299.99
    }
  ]
}
```

#### Get Order by ID
- **GET** `/api/orders/{id}`
- **Description**: Retrieve order by ID

#### Get All Orders
- **GET** `/api/orders`
- **Description**: Retrieve all orders

#### Get Orders by User ID
- **GET** `/api/orders/user/{userId}`
- **Description**: Retrieve all orders for a specific user

#### Update Order
- **PUT** `/api/orders/{id}`
- **Description**: Update order information
- **Request Body**:
```json
{
  "total": 2999.97,
  "status": "PROCESSING",
  "items": [
    {
      "productId": 1,
      "quantity": 3,
      "price": 999.99
    }
  ]
}
```

#### Delete Order
- **DELETE** `/api/orders/{id}`
- **Description**: Delete order by ID

## Data Models

### User
- `id`: Long (auto-generated)
- `name`: String (max 100 characters)
- `email`: String (max 100 characters, unique)
- `password`: String (encrypted)
- `createdAt`: Timestamp
- `updatedAt`: Timestamp

### Product
- `id`: Long (auto-generated)
- `name`: String (max 100 characters)
- `description`: String
- `price`: BigDecimal (>= 0)
- `stock`: Integer (>= 0)
- `createdAt`: Timestamp

### Order
- `id`: Long (auto-generated)
- `userId`: Long (foreign key)
- `total`: BigDecimal (>= 0)
- `status`: Enum (PENDING, PROCESSING, COMPLETED, EXPIRED)
- `items`: List of OrderItem
- `createdAt`: Timestamp
- `updatedAt`: Timestamp

### OrderItem
- `id`: Long (auto-generated)
- `orderId`: Long (foreign key)
- `productId`: Long (foreign key)
- `quantity`: Integer (> 0)
- `price`: BigDecimal (> 0)

## Validation Rules

All DTOs are validated with Jakarta Bean Validation:
- Required fields return 400 if missing
- Email format validation
- String length constraints
- Numeric range validations
- Custom business logic validations

## Error Responses

### 400 Bad Request
Invalid input data or validation errors
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "email": "Email must be valid",
    "name": "Name is required"
  }
}
```

### 401 Unauthorized
Missing or invalid JWT token
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found
Resource not found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 123",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 500 Internal Server Error
Unexpected server error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Security

- All endpoints (except `/api/auth/login`) require JWT authentication
- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- CSRF protection disabled for REST API

## Using JWT Authentication

1. Login to get a JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

2. Use the token in subsequent requests:
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database (H2 for development)
spring.datasource.url=jdbc:h2:mem:ecommerce

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Kafka
spring.kafka.bootstrap-servers=localhost:29092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.group-id=order-service-group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
```

## Event-Driven Architecture with Kafka

This application uses **Apache Kafka** for asynchronous, event-driven order processing. The Kafka integration enables decoupled, scalable, and reliable order management with automated payment processing and expiration handling.

### Overview

The event-driven system implements the following workflow:

1. **Order Creation** → Publishes `OrderCreatedEvent`
2. **Order Processing** → Consumer processes payment (simulated with 5-second delay)
3. **Payment Result** → 50% success rate:
   - Success → Order status: COMPLETED → Publishes `OrderCompletedEvent`
   - Failure → Order remains in PROCESSING
4. **Order Expiration** → Scheduled job expires PROCESSING orders older than 10 minutes → Publishes `OrderExpiredEvent`
5. **Notifications** → Separate consumer handles COMPLETED and EXPIRED events → Stores notifications in database

### Kafka Components

#### Topics
- **`order-events`**: Single topic for all order-related events (OrderCreated, OrderCompleted, OrderExpired)

#### Event Types

**OrderCreatedEvent**
```json
{
  "orderId": 1,
  "userId": 1,
  "total": 1299.99,
  "timestamp": "2025-11-23T10:30:00"
}
```

**OrderCompletedEvent**
```json
{
  "orderId": 1,
  "timestamp": "2025-11-23T10:30:05"
}
```

**OrderExpiredEvent**
```json
{
  "orderId": 2,
  "timestamp": "2025-11-23T10:40:00"
}
```

#### Producers
- **OrderEventPublisher**: Publishes events to `order-events` topic
  - `publishOrderCreated()` - When order is created via POST /api/orders
  - `publishOrderCompleted()` - When payment succeeds
  - `publishOrderExpired()` - When order expires

#### Consumers
- **OrderEventConsumer** (Group: `order-processor-group`)
  - Listens for `OrderCreatedEvent`
  - Updates order status: PENDING → PROCESSING
  - Simulates payment processing (5-second delay)
  - 50% success rate → COMPLETED or remains PROCESSING

- **NotificationService** (Group: `notification-service-group`)
  - Listens for `OrderCompletedEvent` and `OrderExpiredEvent`
  - Logs email notifications to console
  - Persists notifications to database

#### Scheduled Jobs
- **OrderExpirationScheduler**: Runs every 60 seconds
  - Finds PROCESSING orders older than 10 minutes
  - Updates status to EXPIRED
  - Publishes `OrderExpiredEvent`

### Order Status Flow

```
PENDING → PROCESSING → COMPLETED (50% success)
              ↓
           EXPIRED (if > 10 minutes in PROCESSING)
```

### Notifications Database

All order events (COMPLETED and EXPIRED) are persisted in the `notifications` table:

```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### Running with Kafka

#### Using Docker Compose (Recommended)

The project includes a `docker-compose.yml` file that runs Kafka, Zookeeper, PostgreSQL, and the application:

```bash
# Start all services (Kafka, Zookeeper, PostgreSQL, App)
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down
```

Services:
- **Zookeeper**: `localhost:2181`
- **Kafka**: `localhost:29092` (external) / `kafka:9092` (internal)
- **PostgreSQL**: `localhost:5431`
- **Application**: `localhost:8080`

#### Running Locally (Development)

If running the Spring Boot app locally, start Kafka and Zookeeper via Docker:

```bash
# Start only Kafka and Zookeeper
docker-compose up -d zookeeper kafka

# Run the Spring Boot application
mvn spring-boot:run
```

### Testing the Event-Driven Flow

1. **Create an order** (triggers OrderCreatedEvent):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "total": 1299.99,
    "status": "PENDING",
    "items": [{"productId": 1, "quantity": 1, "price": 1299.99}]
  }'
```

2. **Monitor logs** to see:
   - OrderCreatedEvent published
   - Order status updated to PROCESSING
   - Payment simulation (5 seconds)
   - 50% chance: OrderCompletedEvent + notification
   - 50% chance: Order remains PROCESSING (will expire in 10 minutes)

3. **Check notifications**:
```bash
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Kafka Integration Tests

The project includes comprehensive integration tests that verify the entire event-driven flow:

- `KafkaOrderEventIntegrationTest.java`:
  - Tests OrderCreatedEvent publishing
  - Verifies order status transitions (PENDING → PROCESSING)
  - Tests OrderCompletedEvent publishing
  - Verifies notification creation
  - Tests order expiration and OrderExpiredEvent publishing

Run tests:
```bash
mvn test
```

### Kafka Configuration

**Producer Configuration**:
- Key Serializer: `StringSerializer`
- Value Serializer: `JsonSerializer` (automatic JSON conversion)

**Consumer Configuration**:
- Key Deserializer: `StringDeserializer`
- Value Deserializer: `ErrorHandlingDeserializer` with `JsonDeserializer`
- Auto Offset Reset: `earliest` (start from beginning if no offset)
- Consumer Groups: Separate groups for order processing and notifications

### Benefits of Event-Driven Architecture

1. **Decoupling**: Order creation is independent of payment processing
2. **Scalability**: Multiple consumers can process events in parallel
3. **Reliability**: Kafka ensures message delivery and enables replay
4. **Audit Trail**: Complete event history in Kafka logs
5. **Asynchronous**: Non-blocking order creation improves API response times
6. **Resilience**: Failed processing can be retried, expired orders are automatically handled

## Production Deployment

For production, update the database configuration to use PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

Also, update the JWT secret to a strong, random value.

## License

This project is open source and available under the MIT License.

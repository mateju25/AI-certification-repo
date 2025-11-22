# E-commerce REST API

A comprehensive Spring Boot REST API for an e-commerce application with JWT authentication, validation, and complete OpenAPI/Swagger documentation.

## Features

- User management with CRUD operations
- JWT-based authentication and authorization
- Product management
- Order management with order items
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
- Lombok
- Maven
- SpringDoc OpenAPI 3

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
```

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

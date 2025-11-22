# API Documentation - Error Codes and Responses

This document provides a comprehensive overview of all API endpoints and their possible error responses.

## Error Response Format

All error responses follow this standard format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "fieldName": "Error message for this field"
  }
}
```

Note: `validationErrors` is only included for 400 Bad Request responses with validation failures.

## HTTP Status Codes

### 200 OK
Successfully retrieved resource(s)

### 201 Created
Resource successfully created

### 204 No Content
Resource successfully deleted

### 400 Bad Request
- Invalid input data
- Validation errors
- Duplicate email
- Business rule violations

### 401 Unauthorized
- Invalid JWT token
- Expired JWT token
- Missing JWT token
- Invalid login credentials

### 404 Not Found
- Resource not found (User, Product, Order)
- Referenced resource not found (e.g., Product in Order)

### 500 Internal Server Error
- Unexpected server errors
- Database errors
- System failures

---

## Authentication Module

### POST /api/auth/login

**Description**: Authenticate user and receive JWT token

**Possible Responses**:
- `200 OK` - Login successful
- `400 Bad Request` - Invalid input data (missing email/password, invalid format)
- `401 Unauthorized` - Invalid credentials
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 400 - Validation Error
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "email": "Email must be valid",
    "password": "Password is required"
  }
}
```

```json
// 401 - Invalid Credentials
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Users Module

All user endpoints require JWT Bearer token authentication.

### POST /api/users

**Description**: Create a new user

**Possible Responses**:
- `201 Created` - User created successfully
- `400 Bad Request` - Invalid input or email already exists
- `401 Unauthorized` - Invalid/missing JWT token
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 400 - Duplicate Email
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists: user@example.com",
  "timestamp": "2024-01-15T10:30:00"
}
```

```json
// 400 - Validation Error
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "name": "Name must not exceed 100 characters",
    "email": "Email must be valid"
  }
}
```

### GET /api/users/{id}

**Description**: Retrieve user by ID

**Possible Responses**:
- `200 OK` - User found
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - User not found
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 404 - User Not Found
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 123",
  "timestamp": "2024-01-15T10:30:00"
}
```

### GET /api/users

**Description**: Retrieve all users

**Possible Responses**:
- `200 OK` - Users retrieved successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `500 Internal Server Error` - Unexpected error

### PUT /api/users/{id}

**Description**: Update user by ID

**Possible Responses**:
- `200 OK` - User updated successfully
- `400 Bad Request` - Invalid input or email already exists
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - User not found
- `500 Internal Server Error` - Unexpected error

### DELETE /api/users/{id}

**Description**: Delete user by ID

**Possible Responses**:
- `204 No Content` - User deleted successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - User not found
- `500 Internal Server Error` - Unexpected error

---

## Products Module

All product endpoints require JWT Bearer token authentication.

### POST /api/products

**Description**: Create a new product

**Possible Responses**:
- `201 Created` - Product created successfully
- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Invalid/missing JWT token
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 400 - Validation Error
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "price": "Price must be greater than or equal to 0",
    "stock": "Stock must be greater than or equal to 0"
  }
}
```

### GET /api/products/{id}

**Description**: Retrieve product by ID

**Possible Responses**:
- `200 OK` - Product found
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 404 - Product Not Found
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 456",
  "timestamp": "2024-01-15T10:30:00"
}
```

### GET /api/products

**Description**: Retrieve all products

**Possible Responses**:
- `200 OK` - Products retrieved successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `500 Internal Server Error` - Unexpected error

### PUT /api/products/{id}

**Description**: Update product by ID

**Possible Responses**:
- `200 OK` - Product updated successfully
- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Unexpected error

### DELETE /api/products/{id}

**Description**: Delete product by ID

**Possible Responses**:
- `204 No Content` - Product deleted successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Unexpected error

---

## Orders Module

All order endpoints require JWT Bearer token authentication.

### POST /api/orders

**Description**: Create a new order with order items

**Possible Responses**:
- `201 Created` - Order created successfully
- `400 Bad Request` - Invalid input data or validation error
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - User or Product not found
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 400 - Validation Error
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "total": "Total must be greater than or equal to 0",
    "items[0].quantity": "Quantity must be greater than 0",
    "items[0].price": "Price must be greater than 0"
  }
}
```

```json
// 404 - Product Not Found in Order
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 789",
  "timestamp": "2024-01-15T10:30:00"
}
```

### GET /api/orders/{id}

**Description**: Retrieve order by ID including all order items

**Possible Responses**:
- `200 OK` - Order found
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Order not found
- `500 Internal Server Error` - Unexpected error

**Error Examples**:

```json
// 404 - Order Not Found
{
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 999",
  "timestamp": "2024-01-15T10:30:00"
}
```

### GET /api/orders

**Description**: Retrieve all orders including their items

**Possible Responses**:
- `200 OK` - Orders retrieved successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `500 Internal Server Error` - Unexpected error

### GET /api/orders/user/{userId}

**Description**: Retrieve all orders for a specific user

**Possible Responses**:
- `200 OK` - Orders retrieved successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - User not found
- `500 Internal Server Error` - Unexpected error

### PUT /api/orders/{id}

**Description**: Update existing order (status, total, items)

**Possible Responses**:
- `200 OK` - Order updated successfully
- `400 Bad Request` - Invalid input data or validation error
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Order or Product not found
- `500 Internal Server Error` - Unexpected error

### DELETE /api/orders/{id}

**Description**: Delete order and all its items

**Possible Responses**:
- `204 No Content` - Order deleted successfully
- `401 Unauthorized` - Invalid/missing JWT token
- `404 Not Found` - Order not found
- `500 Internal Server Error` - Unexpected error

---

## Accessing Swagger UI

The complete interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

Swagger UI provides:
- Complete endpoint documentation
- Request/response schemas
- Error response examples
- Try-it-out functionality
- JWT authentication configuration

### Using JWT in Swagger UI

1. Login via `/api/auth/login` endpoint
2. Copy the JWT token from the response
3. Click the "Authorize" button at the top of Swagger UI
4. Enter: `Bearer <your-token-here>`
5. Click "Authorize"
6. All authenticated endpoints will now include the JWT token

---

## Common Error Scenarios

### Authentication Errors

**Missing JWT Token**:
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Invalid/Expired JWT Token**:
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Validation Errors

**Multiple Field Errors**:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "validationErrors": {
    "name": "Name is required",
    "email": "Email must be valid",
    "password": "Password is required"
  }
}
```

### Resource Not Found

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: 123",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Server Errors

```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred: [error details]",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Testing Error Responses

### Using cURL

```bash
# Test 401 - Missing JWT token
curl -X GET http://localhost:8080/api/users

# Test 400 - Invalid input
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"","email":"invalid","password":""}'

# Test 404 - Not Found
curl -X GET http://localhost:8080/api/users/99999 \
  -H "Authorization: Bearer <your-token>"

# Test 401 - Invalid credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"wrong@example.com","password":"wrongpass"}'
```

---

## Support

For detailed request/response examples and to test the API interactively, please use the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

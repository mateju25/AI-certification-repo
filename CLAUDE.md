# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Building the Project
```bash
# Clean and build
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Compile only
mvn clean compile
```

### Running the Application
```bash
# Run the Spring Boot application
mvn spring-boot:run

# Application runs on http://localhost:8080
# Swagger UI available at http://localhost:8080/swagger-ui.html
# H2 Console available at http://localhost:8080/h2-console
```

### Testing
```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=EcommerceIntegrationTest

# Run tests with coverage
mvn clean test
```

### Admin User (Auto-seeded)
- **Email**: `admin@ecommerce.com`
- **Password**: `admin123`
- Created automatically by `DataSeeder` on application startup

## Architecture Overview

### Security and Authentication Flow

The application uses JWT-based authentication with a stateless session management approach:

1. **Authentication Entry Point**: All requests pass through `JwtAuthenticationFilter` which:
   - Extracts JWT from `Authorization: Bearer <token>` header
   - Validates token using `JwtUtil`
   - Sets authentication in `SecurityContextHolder`
   - Catches malformed tokens gracefully (logs error, allows filter chain to continue)

2. **Permitted Endpoints** (no authentication required):
   - `/api/auth/**` - Login endpoint
   - `/swagger-ui/**`, `/v3/api-docs/**` - API documentation
   - `/h2-console/**` - Database console

3. **Protected Endpoints**: All other endpoints require valid JWT token

4. **Security Configuration** (`SecurityConfig.java`):
   - CSRF disabled (stateless REST API)
   - Custom authentication entry point for 401 responses
   - BCrypt password encoding
   - Session creation policy: STATELESS

### Error Response Architecture

The application has a unified error handling system through `GlobalExceptionHandler`:

**Error DTOs**:
- `ErrorMessageDTO`: Simple errors with just a message field (used for 401, 404, 500)
- `ValidationErrorDTO`: Contains `message` + `validationErrors` map (used for 400)

**Important**: Error responses do NOT include a `status` field. When writing tests or validating responses:
- ✅ Check HTTP status code directly
- ✅ Check `$.message` field in JSON
- ✅ Check `$.validationErrors` for validation failures
- ❌ Do NOT check for `$.status`, `$.error`, or `$.timestamp` fields

**Exception Mapping**:
- `ResourceNotFoundException` → 404 with `ErrorMessageDTO`
- `DuplicateResourceException` → 400 with `ValidationErrorDTO`
- `UnauthorizedException`, `BadCredentialsException` → 401 with `ErrorMessageDTO` ("Invalid credentials")
- `MethodArgumentNotValidException` → 400 with `ValidationErrorDTO` ("Validation failed")
- All others → 500 with `ErrorMessageDTO`

### Order Status Enum Values

The `OrderStatus` enum contains exactly these values:
- `PENDING`
- `PROCESSING`
- `COMPLETED`
- `EXPIRED`

**Note**: There are NO `SHIPPED` or `DELIVERED` statuses. Use the correct values when creating/updating orders.

### Entity Relationships

```
User (1) ----< (N) Order (1) ----< (N) OrderItem (N) >---- (1) Product

User:
- Has many Orders (via userId foreign key)

Order:
- Belongs to one User
- Has many OrderItems (cascade ALL, orphan removal)
- OrderItems deleted when Order is deleted

OrderItem:
- Belongs to one Order
- References one Product (productId foreign key, no cascade)
```

**Cascade Behavior**:
- Deleting an Order deletes all its OrderItems
- Deleting a Product does NOT delete OrderItems (productId is just a reference)
- Deleting a User does NOT automatically delete their Orders (manual cleanup required)

### DTO Layer Architecture

The application follows a strict DTO pattern:

**Separation of DTOs**:
- `*RequestDTO`: Input validation, used for CREATE/UPDATE operations
- `*ResponseDTO`: Output serialization, what the API returns
- `*UpdateDTO`: Partial updates (typically subset of RequestDTO)

**Validation**:
- All Request DTOs use Jakarta Bean Validation annotations
- Validation happens at controller level via `@Valid`
- Field-level validations: `@NotBlank`, `@Email`, `@Size`, `@Min`, `@DecimalMin`, `@NotNull`
- Validation errors collected and returned in `ValidationErrorDTO`

### Service Layer Patterns

Services handle:
1. **Business logic validation**: Checking if email exists, if product exists, etc.
2. **Entity-DTO mapping**: Converting between entities and DTOs
3. **Exception throwing**: `ResourceNotFoundException`, `DuplicateResourceException`
4. **Transaction management**: Implicit via Spring's `@Transactional` (at repository level)

**Password Handling**:
- Passwords encrypted using BCrypt in Service layer
- `PasswordEncoder` injected into services that need it
- Passwords never returned in Response DTOs

## Integration Testing Patterns

When writing integration tests for this codebase:

### Test Setup
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
```

### JWT Token Management
- Use `@BeforeAll` to login once and store JWT token
- Login with: `admin@ecommerce.com` / `admin123`
- Share token across all tests via instance variable (due to `PER_CLASS` lifecycle)

### Test Profile Configuration
- Tests use H2 in-memory database (configured in `application-test.properties`)
- DataSeeder still runs, creating admin user
- Each test run starts with fresh database

### Common Patterns
- Always include `Authorization: Bearer <token>` header for protected endpoints
- Test validation errors by checking `$.message` and `$.validationErrors`
- Test 404s by checking `$.message` contains entity type
- Tests should be ordered and build on each other (create → read → update → delete)

## Project Structure Notes

### Config Package
- `SecurityConfig`: Spring Security setup, filter chain
- `OpenApiConfig`: Swagger/OpenAPI configuration
- `DataSeeder`: CommandLineRunner that seeds admin user on startup

### Security Package
- `JwtUtil`: Token generation, validation, claims extraction
- `JwtAuthenticationFilter`: Intercepts requests, validates JWT
- `CustomUserDetailsService`: Loads users for authentication
- `CustomAuthenticationEntryPoint`: Handles authentication failures

### Important Files
- `API_DOCUMENTATION.md`: Complete API documentation with error codes
- `README.md`: Getting started guide, endpoint reference
- `pom.xml`: Maven dependencies (Java 17, Spring Boot 3.2.1, JWT 0.12.3)

## Common Gotchas

1. **Error Response Structure**: Error responses use `ErrorMessageDTO` or `ValidationErrorDTO`, NOT a standardized error response with status/error/timestamp fields
2. **Order Status**: Only 4 valid statuses (PENDING, PROCESSING, COMPLETED, EXPIRED)
3. **Admin Credentials**: Auto-seeded with email `admin@ecommerce.com`, not `admin@example.com`
4. **JWT Header Format**: Must be `Bearer <token>`, not just `<token>`
5. **Integration Test Lifecycle**: Use `PER_CLASS` to share JWT token across tests
6. **Database IDs**: Auto-generated, don't hardcode in test data

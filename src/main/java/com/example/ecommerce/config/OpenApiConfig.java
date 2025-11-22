package com.example.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "E-commerce REST API",
                version = "1.0.0",
                description = "REST API for E-commerce application with JWT authentication",
                contact = @Contact(
                        name = "API Support",
                        email = "support@ecommerce.com"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Bearer Token Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

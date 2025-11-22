package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ErrorResponseDTO;
import com.example.ecommerce.dto.user.UserRequestDTO;
import com.example.ecommerce.dto.user.UserResponseDTO;
import com.example.ecommerce.dto.user.UserUpdateDTO;
import com.example.ecommerce.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided information. Email must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input or email already exists",
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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.createUser(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO responseDTO = userService.getUserById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
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
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Updates an existing user's information. Email must be unique if changed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserResponseDTO responseDTO = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

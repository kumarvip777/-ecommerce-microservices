package com.kumar.userservice.controller;

import com.kumar.userservice.dto.UpdateUserDTO;
import com.kumar.userservice.dto.UserRequestDTO;
import com.kumar.userservice.dto.UserResponseDTO;
import com.kumar.userservice.payload.ApiResponse;
import com.kumar.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "User Management",
        description = "APIs for managing users in E-Commerce Application"
)
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Operation(
            summary = "Register User",
            description = "Creates a new customer account"
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO requestDTO,
            HttpServletRequest request) {

        logger.info("Received request to register user");

        UserResponseDTO savedUser =
                userService.createUser(requestDTO);

        ApiResponse<UserResponseDTO> response =
                ApiResponse.success(
                        savedUser,
                        "User registered successfully",
                        request.getRequestURI()
                );

        logger.info("User registered successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get User By Id",
            description = "Returns user details by user id"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long userId,
            HttpServletRequest request) {

        logger.info("Received request to fetch user: {}", userId);

        UserResponseDTO user =
                userService.getUserById(userId);

        ApiResponse<UserResponseDTO> response =
                ApiResponse.success(
                        user,
                        "User fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get All Users",
            description = "Returns paginated list of users"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "userId") String sortBy,

            @RequestParam(defaultValue = "asc") String direction,

            HttpServletRequest request) {

        logger.info("Received request to fetch all users");

        Page<UserResponseDTO> users =
                userService.getAllUsers(
                        page,
                        size,
                        sortBy,
                        direction
                );

        ApiResponse<Page<UserResponseDTO>> response =
                ApiResponse.success(
                        users,
                        "Users fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update User",
            description = "Updates an existing user"
    )
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(

            @PathVariable Long userId,

            @Valid
            @RequestBody UpdateUserDTO updateDTO,

            HttpServletRequest request) {

        logger.info("Received request to update user: {}", userId);

        UserResponseDTO updatedUser =
                userService.updateUser(
                        userId,
                        updateDTO
                );

        ApiResponse<UserResponseDTO> response =
                ApiResponse.success(
                        updatedUser,
                        "User updated successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete User",
            description = "Deletes user by id"
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(

            @PathVariable Long userId,

            HttpServletRequest request) {

        logger.info("Received request to delete user: {}", userId);

        userService.deleteUser(userId);

        ApiResponse<String> response =
                ApiResponse.success(
                        "User deleted successfully",
                        "User deleted successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
}
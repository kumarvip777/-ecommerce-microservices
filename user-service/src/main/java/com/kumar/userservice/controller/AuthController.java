package com.kumar.userservice.controller;

import com.kumar.userservice.dto.LoginRequest;
import com.kumar.userservice.dto.LoginResponse;
import com.kumar.userservice.payload.ApiResponse;
import com.kumar.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication Management",
        description = "APIs for user authentication in E-Commerce Application"
)
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Operation(
            summary = "User Login",
            description = "Authenticates the user and returns JWT token"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        logger.info("Received login request for email : {}",
                loginRequest.getEmail());

        LoginResponse loginResponse =
                authService.login(loginRequest);
        ApiResponse<LoginResponse> response =
                ApiResponse.success(
                        loginResponse,
                        "Login successful",
                        request.getRequestURI()
                );

        logger.info("User login successful");

        return ResponseEntity.ok(response);
    }
}
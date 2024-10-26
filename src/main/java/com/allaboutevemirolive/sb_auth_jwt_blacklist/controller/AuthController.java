package com.allaboutevemirolive.sb_auth_jwt_blacklist.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.allaboutevemirolive.sb_auth_jwt_blacklist.config.jwt.JwtUtil;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.dto.request.LoginRequest;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.dto.response.ApiResponse;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.dto.response.LoginResponse;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.service.TokenBlacklistService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
                String token = jwtUtil.generateToken(loginRequest.getUsername());
                LoginResponse loginResponse = new LoginResponse(token);
                return ResponseEntity.ok(new ApiResponse(true, "Login successful", loginResponse));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid credentials"));
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid token format"));
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token"));
            }

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Token already invalidated"));
            }

            tokenBlacklistService.blacklistToken(token);
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(new ApiResponse(true, "Logout successful"));
        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid token format"));
            }

            String token = authHeader.substring(7);

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Token has been invalidated"));
            }

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                return ResponseEntity.ok(new ApiResponse(true, "Token is valid", username));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or expired token"));
        } catch (Exception e) {
            log.error("Token validation error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Validation failed: " + e.getMessage()));
        }
    }
}

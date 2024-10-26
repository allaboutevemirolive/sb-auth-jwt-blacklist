package com.allaboutevemirolive.sb_auth_jwt_blacklist.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allaboutevemirolive.sb_auth_jwt_blacklist.dto.response.ApiResponse;
import com.allaboutevemirolive.sb_auth_jwt_blacklist.service.TokenBlacklistService;

@RestController
@RequestMapping("/api/admin/blacklist")
public class BlacklistAdminController {

    private static final Logger log = LoggerFactory.getLogger(BlacklistAdminController.class);

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @GetMapping("/tokens")
    public ResponseEntity<ApiResponse> getAllBlacklistedTokens() {
        try {
            List<String> tokens = tokenBlacklistService.getAllBlacklistedTokens();
            return ResponseEntity.ok(new ApiResponse(true, "Retrieved blacklisted tokens", tokens));
        } catch (Exception e) {
            log.error("Error retrieving blacklisted tokens", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to retrieve blacklisted tokens: " + e.getMessage()));
        }
    }

    @GetMapping("/tokens/details")
    public ResponseEntity<ApiResponse> getAllBlacklistedTokensWithDetails() {
        try {
            Map<String, String> tokenUserMap = tokenBlacklistService.getAllBlacklistedTokensWithUsernames();

            // Add TTL information
            List<Map<String, Object>> detailedInfo = new ArrayList<>();

            tokenUserMap.forEach((token, username) -> {
                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("token", token);
                tokenInfo.put("username", username);
                tokenInfo.put("ttl", tokenBlacklistService.getTokenTTL(token));
                detailedInfo.add(tokenInfo);
            });

            return ResponseEntity.ok(new ApiResponse(true, "Retrieved blacklisted tokens with details", detailedInfo));
        } catch (Exception e) {
            log.error("Error retrieving blacklisted token details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to retrieve token details: " + e.getMessage()));
        }
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<ApiResponse> getTokenDetails(@PathVariable String token) {
        try {
            if (!tokenBlacklistService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Token not found in blacklist"));
            }

            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("token", token);
            tokenInfo.put("username", tokenBlacklistService.getBlacklistedTokenUsername(token));
            tokenInfo.put("ttl", tokenBlacklistService.getTokenTTL(token));

            return ResponseEntity.ok(new ApiResponse(true, "Token details retrieved", tokenInfo));
        } catch (Exception e) {
            log.error("Error retrieving token details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to retrieve token details: " + e.getMessage()));
        }
    }

    @DeleteMapping("/token/{token}")
    public ResponseEntity<ApiResponse> removeTokenFromBlacklist(@PathVariable String token) {
        try {
            if (!tokenBlacklistService.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Token not found in blacklist"));
            }

            boolean removed = tokenBlacklistService.removeFromBlacklist(token);
            if (removed) {
                return ResponseEntity.ok(new ApiResponse(true, "Token removed from blacklist"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(false, "Failed to remove token from blacklist"));
            }
        } catch (Exception e) {
            log.error("Error removing token from blacklist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to remove token: " + e.getMessage()));
        }
    }
}

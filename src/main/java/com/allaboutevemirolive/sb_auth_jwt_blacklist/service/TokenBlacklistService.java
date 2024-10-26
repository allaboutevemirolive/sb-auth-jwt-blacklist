package com.allaboutevemirolive.sb_auth_jwt_blacklist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.allaboutevemirolive.sb_auth_jwt_blacklist.config.jwt.JwtUtil;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    public void blacklistToken(String token) {
        String username = jwtUtil.extractUsername(token);
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, username, 24, TimeUnit.HOURS);
        log.info("Token blacklisted for user: {}", username);
    }

    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Get username associated with blacklisted token
    public String getBlacklistedTokenUsername(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    // Get all blacklisted tokens
    public List<String> getAllBlacklistedTokens() {
        Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
        if (keys == null) {
            return new ArrayList<>();
        }

        List<String> tokens = new ArrayList<>();
        for (String key : keys) {
            tokens.add(key.substring(BLACKLIST_PREFIX.length()));
        }
        return tokens;
    }

    // Get all blacklisted tokens with their associated usernames
    public Map<String, String> getAllBlacklistedTokensWithUsernames() {
        Set<String> keys = redisTemplate.keys(BLACKLIST_PREFIX + "*");
        if (keys == null) {
            return new HashMap<>();
        }

        Map<String, String> tokenUserMap = new HashMap<>();
        keys.forEach(key -> {
            String token = key.substring(BLACKLIST_PREFIX.length());
            String username = redisTemplate.opsForValue().get(key);
            tokenUserMap.put(token, username);
        });
        return tokenUserMap;
    }

    // Get remaining time-to-live for a blacklisted token
    public Long getTokenTTL(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // Remove token from blacklist (for testing/admin purposes)
    public boolean removeFromBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}

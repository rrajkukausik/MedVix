package com.medivex.user.service.service;

import com.medivex.user.service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    public void blacklistToken(String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                // Calculate remaining time until token expires
                long expirationTime = jwtUtil.extractExpiration(token).getTime();
                long currentTime = System.currentTimeMillis();
                long ttl = (expirationTime - currentTime) / 1000; // Convert to seconds
                
                if (ttl > 0) {
                    String key = BLACKLIST_PREFIX + token;
                    redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.SECONDS);
                    log.info("Token blacklisted successfully");
                } else {
                    log.warn("Token is already expired, no need to blacklist");
                }
            } else {
                log.warn("Invalid token provided for blacklisting");
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }
    
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check token blacklist: {}", e.getMessage());
            return false;
        }
    }
    
    public void removeFromBlacklist(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.delete(key);
            log.info("Token removed from blacklist");
        } catch (Exception e) {
            log.error("Failed to remove token from blacklist: {}", e.getMessage());
        }
    }
}

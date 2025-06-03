package com.example.HeattKeeper.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStorage {
    private final Map<Long, String> userRefreshTokens = new ConcurrentHashMap<>();

    public void storeToken(Long userId, String refreshToken) {
        userRefreshTokens.put(userId, refreshToken);
    }

    public boolean isTokenValid(Long userId, String refreshToken) {
        return refreshToken.equals(userRefreshTokens.get(userId));
    }

    public void invalidateToken(Long userId) {
        userRefreshTokens.remove(userId);
    }
}

package com.taskviewer.api.view;

import java.time.Instant;

public class TokenManager {
    private static TokenManager instance;
    private String accessToken;
    private String refreshToken;
    private Instant accessTokenExpiration;
    private Instant refreshTokenExpiration;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public synchronized void setTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiration = Instant.now().plusSeconds(20 * 60); // 20 minutes from now
        this.refreshTokenExpiration = Instant.now().plusSeconds(24 * 60 * 60); // 24 hours from now
    }

    public synchronized String getAccessToken() {
        if (accessTokenExpiration != null && accessTokenExpiration.isBefore(Instant.now())) {
            accessToken = null;
            accessTokenExpiration = null;
        }
        return accessToken;
    }

    public synchronized String getRefreshToken() {
        if (refreshTokenExpiration != null && refreshTokenExpiration.isBefore(Instant.now())) {
            refreshToken = null;
            refreshTokenExpiration = null;
        }
        return refreshToken;
    }

    public synchronized boolean hasTokens() {
        return accessToken != null && refreshToken != null && accessTokenExpiration != null && refreshTokenExpiration != null
                && accessTokenExpiration.isAfter(Instant.now()) && refreshTokenExpiration.isAfter(Instant.now());
    }

    public synchronized void clearTokens() {
        accessToken = null;
        refreshToken = null;
        accessTokenExpiration = null;
        refreshTokenExpiration = null;
    }
}

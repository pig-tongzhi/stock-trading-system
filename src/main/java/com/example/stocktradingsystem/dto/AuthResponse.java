package com.example.stocktradingsystem.dto;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        String nickname,
        AccountResponse account
) {
}

package com.example.stocktradingsystem.security;

public record AuthenticatedUser(
        Long userId,
        String username
) {
}

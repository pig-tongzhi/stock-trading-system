package com.example.stocktradingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        Long userId,
        String accountName,
        BigDecimal availableBalance,
        BigDecimal frozenBalance,
        BigDecimal initialAsset,
        LocalDateTime createdAt
) {
}

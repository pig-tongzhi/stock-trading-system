package com.example.stocktradingsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockQuoteResponse(
        String code,
        String name,
        BigDecimal latestPrice,
        BigDecimal previousClose,
        BigDecimal changeRate,
        Boolean tradingEnabled,
        LocalDateTime updatedAt
) {
}

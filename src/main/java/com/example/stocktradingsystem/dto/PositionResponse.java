package com.example.stocktradingsystem.dto;

import java.math.BigDecimal;

public record PositionResponse(
        String stockCode,
        String stockName,
        Integer quantity,
        Integer availableQuantity,
        BigDecimal averageCost,
        BigDecimal latestPrice,
        BigDecimal marketValue,
        BigDecimal unrealizedProfit,
        BigDecimal realizedProfit
) {
}

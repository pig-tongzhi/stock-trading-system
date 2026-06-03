package com.example.stocktradingsystem.dto;

import com.example.stocktradingsystem.entity.OrderSide;
import com.example.stocktradingsystem.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeOrderResponse(
        Long orderId,
        String stockCode,
        String stockName,
        OrderSide side,
        OrderStatus status,
        BigDecimal price,
        Integer quantity,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}

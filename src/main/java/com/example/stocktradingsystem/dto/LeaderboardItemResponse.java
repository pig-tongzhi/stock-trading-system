package com.example.stocktradingsystem.dto;

import java.math.BigDecimal;

public record LeaderboardItemResponse(
        Integer rank,
        String nickname,
        BigDecimal totalAsset,
        BigDecimal totalProfit,
        BigDecimal profitRate
) {
}

package com.example.stocktradingsystem.dto;

import java.math.BigDecimal;
import java.util.List;

public record AssetSummaryResponse(
        AccountResponse account,
        BigDecimal positionMarketValue,
        BigDecimal totalAsset,
        BigDecimal totalProfit,
        BigDecimal profitRate,
        List<PositionResponse> positions
) {
}

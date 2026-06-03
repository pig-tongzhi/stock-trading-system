package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.dto.StockQuoteResponse;
import com.example.stocktradingsystem.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market-data")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    @GetMapping("/quotes")
    public ApiResponse<List<StockQuoteResponse>> listQuotes() {
        return ApiResponse.success(marketDataService.listQuotes());
    }

    @PostMapping("/refresh")
    public ApiResponse<Void> refreshLatestQuotes() {
        marketDataService.refreshLatestQuotes();
        return ApiResponse.success(null);
    }
}

package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.dto.TradeOrderResponse;
import com.example.stocktradingsystem.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/orders")
    public ApiResponse<TradeOrderResponse> placeOrder(@Valid @RequestBody OrderPlaceRequest request) {
        return ApiResponse.success(tradingService.placeOrder(request));
    }

    @DeleteMapping("/orders/{orderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable Long orderId) {
        tradingService.cancelOrder(orderId);
        return ApiResponse.success(null);
    }
}

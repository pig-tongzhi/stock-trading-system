package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.dto.TradeOrderResponse;

public interface TradingService {

    TradeOrderResponse placeOrder(OrderPlaceRequest request);

    void cancelOrder(Long orderId);
}

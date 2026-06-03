package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.Position;
import com.example.stocktradingsystem.entity.Stock;

import java.math.BigDecimal;
import java.util.Optional;

public interface RiskControlService {

    void validateOrder(OrderPlaceRequest request, Account account, Stock stock, Optional<Position> position, BigDecimal amount);
}

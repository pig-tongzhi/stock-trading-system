package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.OrderSide;
import com.example.stocktradingsystem.entity.Position;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.exception.BusinessException;
import com.example.stocktradingsystem.service.RiskControlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

// 风控校验：独立模块，和交易逻辑解耦
// 校验项：股票可交易性、单笔数量上限（默认1w）、单笔金额上限（默认100w）、
//        买入余额是否充足、卖出持仓是否足够
// 所有阈值通过 application.yml 配置，无需改代码即可调整
@Service
public class RiskControlServiceImpl implements RiskControlService {

    @Value("${app.risk.max-order-quantity}")
    private int maxOrderQuantity;

    @Value("${app.risk.max-order-amount}")
    private BigDecimal maxOrderAmount;

    @Override
    public void validateOrder(OrderPlaceRequest request, Account account, Stock stock, Optional<Position> position, BigDecimal amount) {
        if (!Boolean.TRUE.equals(stock.getTradingEnabled())) {
            throw new BusinessException("Stock trading is disabled.");
        }
        if (request.quantity() > maxOrderQuantity) {
            throw new BusinessException("Order quantity exceeds risk limit.");
        }
        if (amount.compareTo(maxOrderAmount) > 0) {
            throw new BusinessException("Order amount exceeds risk limit.");
        }
        if (request.side() == OrderSide.BUY && account.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient available balance.");
        }
        if (request.side() == OrderSide.SELL) {
            Position currentPosition = position.orElseThrow(() -> new BusinessException("No position available to sell."));
            if (currentPosition.getAvailableQuantity() < request.quantity()) {
                throw new BusinessException("Insufficient available position.");
            }
        }
    }
}

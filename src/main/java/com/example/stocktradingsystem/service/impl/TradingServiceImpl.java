package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.OrderPlaceRequest;
import com.example.stocktradingsystem.dto.TradeOrderResponse;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.OrderStatus;
import com.example.stocktradingsystem.entity.OrderSide;
import com.example.stocktradingsystem.entity.Position;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.entity.TradeOrder;
import com.example.stocktradingsystem.entity.TradeRecord;
import com.example.stocktradingsystem.exception.BusinessException;
import com.example.stocktradingsystem.repository.AccountRepository;
import com.example.stocktradingsystem.repository.PositionRepository;
import com.example.stocktradingsystem.repository.StockRepository;
import com.example.stocktradingsystem.repository.TradeOrderRepository;
import com.example.stocktradingsystem.repository.TradeRecordRepository;
import com.example.stocktradingsystem.security.CurrentUserService;
import com.example.stocktradingsystem.service.RiskControlService;
import com.example.stocktradingsystem.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final PositionRepository positionRepository;
    private final TradeOrderRepository tradeOrderRepository;
    private final TradeRecordRepository tradeRecordRepository;
    private final RiskControlService riskControlService;
    private final CurrentUserService currentUserService;

    // 核心交易入口：在一个事务内完成下单、资金扣除、持仓更新、成交记录写入
    // 买入：扣现金 → 新建/追加持仓（加权平均成本）
    // 卖出：加现金 → 扣减持仓 → 计算已实现盈亏
    @Override
    @Transactional
    public TradeOrderResponse placeOrder(OrderPlaceRequest request) {
        Long userId = currentUserService.currentUser().userId();
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Account not found."));
        Stock stock = stockRepository.findById(request.stockCode())
                .orElseThrow(() -> new BusinessException("Stock not found."));
        Optional<Position> optionalPosition = positionRepository.findByAccountIdAndStockCode(account.getId(), stock.getCode());
        BigDecimal amount = request.price().multiply(BigDecimal.valueOf(request.quantity())).setScale(4, RoundingMode.HALF_UP);
        riskControlService.validateOrder(request, account, stock, optionalPosition, amount);

        LocalDateTime now = LocalDateTime.now();
        TradeOrder order = new TradeOrder();
        order.setAccountId(account.getId());
        order.setUserId(userId);
        order.setStockCode(request.stockCode());
        order.setStockName(stock.getName());
        order.setSide(request.side());
        order.setPrice(request.price());
        order.setQuantity(request.quantity());
        order.setAmount(amount);
        order.setStatus(OrderStatus.FILLED);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        TradeOrder savedOrder = tradeOrderRepository.save(order);

        if (request.side() == OrderSide.BUY) {
            handleBuy(account, stock, optionalPosition, request.quantity(), request.price(), amount, now);
        } else {
            handleSell(account, stock, optionalPosition.orElseThrow(), request.quantity(), request.price(), amount, now);
        }

        TradeRecord record = new TradeRecord();
        record.setOrderId(savedOrder.getId());
        record.setAccountId(account.getId());
        record.setUserId(userId);
        record.setStockCode(stock.getCode());
        record.setStockName(stock.getName());
        record.setSide(request.side());
        record.setPrice(request.price());
        record.setQuantity(request.quantity());
        record.setAmount(amount);
        record.setTradedAt(now);
        tradeRecordRepository.save(record);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Long userId = currentUserService.currentUser().userId();
        TradeOrder order = tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found."));
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("Order does not belong to current user.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        tradeOrderRepository.save(order);
    }

    // 买入处理：扣减可用余额，计算加权平均持仓成本
    // 加权平均成本 = (原持仓市值 + 新买入金额) / 总持仓数量
    private void handleBuy(Account account, Stock stock, Optional<Position> optionalPosition, Integer quantity,
                           BigDecimal price, BigDecimal amount, LocalDateTime now) {
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        account.setUpdatedAt(now);
        accountRepository.save(account);

        Position position = optionalPosition.orElseGet(() -> {
            Position created = new Position();
            created.setAccountId(account.getId());
            created.setStockCode(stock.getCode());
            created.setStockName(stock.getName());
            created.setQuantity(0);
            created.setAvailableQuantity(0);
            created.setAverageCost(BigDecimal.ZERO);
            created.setRealizedProfit(BigDecimal.ZERO);
            created.setCreatedAt(now);
            return created;
        });

        BigDecimal oldCost = position.getAverageCost().multiply(BigDecimal.valueOf(position.getQuantity()));
        BigDecimal newCost = oldCost.add(amount);
        int newQuantity = position.getQuantity() + quantity;
        position.setQuantity(newQuantity);
        position.setAvailableQuantity(position.getAvailableQuantity() + quantity);
        position.setAverageCost(newCost.divide(BigDecimal.valueOf(newQuantity), 4, RoundingMode.HALF_UP));
        position.setUpdatedAt(now);
        positionRepository.save(position);
    }

    // 卖出处理：资金入账，计算已实现盈亏 = 卖出金额 - 卖出数量 × 加权平均成本
    private void handleSell(Account account, Stock stock, Position position, Integer quantity,
                            BigDecimal price, BigDecimal amount, LocalDateTime now) {
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
        account.setUpdatedAt(now);
        accountRepository.save(account);

        BigDecimal cost = position.getAverageCost().multiply(BigDecimal.valueOf(quantity));
        BigDecimal realizedProfit = amount.subtract(cost);
        position.setQuantity(position.getQuantity() - quantity);
        position.setAvailableQuantity(position.getAvailableQuantity() - quantity);
        position.setRealizedProfit(position.getRealizedProfit().add(realizedProfit));
        position.setUpdatedAt(now);
        position.setStockName(stock.getName());
        positionRepository.save(position);
    }

    private TradeOrderResponse toResponse(TradeOrder order) {
        return new TradeOrderResponse(
                order.getId(),
                order.getStockCode(),
                order.getStockName(),
                order.getSide(),
                order.getStatus(),
                order.getPrice(),
                order.getQuantity(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }
}

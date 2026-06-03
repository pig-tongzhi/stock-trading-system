package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.AccountResponse;
import com.example.stocktradingsystem.dto.AssetSummaryResponse;
import com.example.stocktradingsystem.dto.PositionResponse;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.Position;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.exception.BusinessException;
import com.example.stocktradingsystem.repository.AccountRepository;
import com.example.stocktradingsystem.repository.PositionRepository;
import com.example.stocktradingsystem.repository.StockRepository;
import com.example.stocktradingsystem.security.CurrentUserService;
import com.example.stocktradingsystem.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final StockRepository stockRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> listCurrentPositions() {
        Account account = currentAccount();
        return positionRepository.findByAccountIdAndQuantityGreaterThan(account.getId(), 0)
                .stream()
                .map(this::toPositionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssetSummaryResponse getCurrentAssetSummary() {
        Account account = currentAccount();
        List<PositionResponse> positions = positionRepository.findByAccountIdAndQuantityGreaterThan(account.getId(), 0)
                .stream()
                .map(this::toPositionResponse)
                .toList();
        BigDecimal marketValue = positions.stream()
                .map(PositionResponse::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAsset = account.getAvailableBalance().add(account.getFrozenBalance()).add(marketValue);
        BigDecimal totalProfit = totalAsset.subtract(account.getInitialAsset());
        BigDecimal profitRate = account.getInitialAsset().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalProfit.divide(account.getInitialAsset(), 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(4, RoundingMode.HALF_UP);

        return new AssetSummaryResponse(toAccountResponse(account), marketValue, totalAsset, totalProfit, profitRate, positions);
    }

    private Account currentAccount() {
        return accountRepository.findByUserId(currentUserService.currentUser().userId())
                .orElseThrow(() -> new BusinessException("Account not found."));
    }

    private PositionResponse toPositionResponse(Position position) {
        Stock stock = stockRepository.findById(position.getStockCode())
                .orElseThrow(() -> new BusinessException("Stock not found."));
        BigDecimal marketValue = stock.getLatestPrice()
                .multiply(BigDecimal.valueOf(position.getQuantity()))
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal cost = position.getAverageCost()
                .multiply(BigDecimal.valueOf(position.getQuantity()))
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal unrealizedProfit = marketValue.subtract(cost);

        return new PositionResponse(
                position.getStockCode(),
                position.getStockName(),
                position.getQuantity(),
                position.getAvailableQuantity(),
                position.getAverageCost(),
                stock.getLatestPrice(),
                marketValue,
                unrealizedProfit,
                position.getRealizedProfit()
        );
    }

    private AccountResponse toAccountResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getAccountName(),
                account.getAvailableBalance(),
                account.getFrozenBalance(),
                account.getInitialAsset(),
                account.getCreatedAt()
        );
    }
}

package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.LeaderboardItemResponse;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.AppUser;
import com.example.stocktradingsystem.entity.Position;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.repository.AccountRepository;
import com.example.stocktradingsystem.repository.AppUserRepository;
import com.example.stocktradingsystem.repository.PositionRepository;
import com.example.stocktradingsystem.repository.StockRepository;
import com.example.stocktradingsystem.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final AccountRepository accountRepository;
    private final AppUserRepository appUserRepository;
    private final PositionRepository positionRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardItemResponse> topProfitUsers(int limit) {
        AtomicInteger rank = new AtomicInteger(1);
        return accountRepository.findAll()
                .stream()
                .map(this::toLeaderboardItem)
                .sorted(Comparator.comparing(LeaderboardItemResponse::profitRate).reversed())
                .limit(Math.max(1, limit))
                .map(item -> new LeaderboardItemResponse(
                        rank.getAndIncrement(),
                        item.nickname(),
                        item.totalAsset(),
                        item.totalProfit(),
                        item.profitRate()
                ))
                .toList();
    }

    private LeaderboardItemResponse toLeaderboardItem(Account account) {
        BigDecimal marketValue = positionRepository.findByAccountIdAndQuantityGreaterThan(account.getId(), 0)
                .stream()
                .map(this::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAsset = account.getAvailableBalance().add(account.getFrozenBalance()).add(marketValue);
        BigDecimal totalProfit = totalAsset.subtract(account.getInitialAsset());
        BigDecimal profitRate = account.getInitialAsset().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalProfit.divide(account.getInitialAsset(), 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(4, RoundingMode.HALF_UP);
        String nickname = appUserRepository.findById(account.getUserId())
                .map(AppUser::getNickname)
                .orElse("Unknown User");
        return new LeaderboardItemResponse(0, nickname, totalAsset, totalProfit, profitRate);
    }

    private BigDecimal marketValue(Position position) {
        return stockRepository.findById(position.getStockCode())
                .map(Stock::getLatestPrice)
                .orElse(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(position.getQuantity()))
                .setScale(4, RoundingMode.HALF_UP);
    }
}

package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.StockQuoteResponse;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.repository.StockRepository;
import com.example.stocktradingsystem.service.MarketDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final StockRepository stockRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.market.cache-key}")
    private String quoteCacheKey;

    @Override
    @Transactional(readOnly = true)
    public List<StockQuoteResponse> listQuotes() {
        List<StockQuoteResponse> cached = getCachedQuotes();
        if (!cached.isEmpty()) {
            return cached;
        }
        List<StockQuoteResponse> quotes = stockRepository.findAll()
                .stream()
                .map(this::toQuoteResponse)
                .toList();
        cacheQuotes(quotes);
        return quotes;
    }

    @Override
    @Scheduled(fixedRateString = "${app.market.refresh-rate-ms}", initialDelay = 10000)
    @Transactional
    public void refreshLatestQuotes() {
        if (stockRepository.count() == 0) {
            initDemoStocks();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            BigDecimal randomRate = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(-5.0, 5.0))
                    .setScale(4, RoundingMode.HALF_UP);
            BigDecimal nextPrice = stock.getLatestPrice()
                    .multiply(BigDecimal.ONE.add(randomRate.divide(ONE_HUNDRED, 8, RoundingMode.HALF_UP)))
                    .max(new BigDecimal("1.00"))
                    .setScale(4, RoundingMode.HALF_UP);
            stock.setPreviousClose(stock.getLatestPrice());
            stock.setLatestPrice(nextPrice);
            stock.setChangeRate(randomRate);
            stock.setUpdatedAt(now);
        }
        stockRepository.saveAll(stocks);
        cacheQuotes(stocks.stream().map(this::toQuoteResponse).toList());
    }

    private void initDemoStocks() {
        LocalDateTime now = LocalDateTime.now();
        List<Stock> stocks = List.of(
                stock("600519", "Kweichow Moutai", "1688.00", now),
                stock("000001", "Ping An Bank", "11.20", now),
                stock("300750", "CATL", "192.50", now),
                stock("600036", "China Merchants Bank", "35.80", now),
                stock("601318", "Ping An Insurance", "42.60", now),
                stock("002594", "BYD", "218.30", now),
                stock("000858", "Wuliangye", "138.40", now),
                stock("600276", "Hengrui Medicine", "46.70", now)
        );
        stockRepository.saveAll(stocks);
    }

    private Stock stock(String code, String name, String price, LocalDateTime now) {
        Stock stock = new Stock();
        stock.setCode(code);
        stock.setName(name);
        stock.setLatestPrice(new BigDecimal(price));
        stock.setPreviousClose(new BigDecimal(price));
        stock.setChangeRate(BigDecimal.ZERO);
        stock.setTradingEnabled(true);
        stock.setUpdatedAt(now);
        return stock;
    }

    private List<StockQuoteResponse> getCachedQuotes() {
        try {
            String json = redisTemplate.opsForValue().get(quoteCacheKey);
            if (json == null || json.isBlank()) {
                return List.of();
            }
            return objectMapper.readValue(json, new TypeReference<List<StockQuoteResponse>>() {
            });
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private void cacheQuotes(List<StockQuoteResponse> quotes) {
        try {
            redisTemplate.opsForValue().set(quoteCacheKey, objectMapper.writeValueAsString(quotes), Duration.ofSeconds(10));
        } catch (Exception ignored) {
            // Redis 不可用时，MySQL 仍然作为最终数据源。
        }
    }

    private StockQuoteResponse toQuoteResponse(Stock stock) {
        return new StockQuoteResponse(
                stock.getCode(),
                stock.getName(),
                stock.getLatestPrice(),
                stock.getPreviousClose(),
                stock.getChangeRate(),
                stock.getTradingEnabled(),
                stock.getUpdatedAt()
        );
    }
}

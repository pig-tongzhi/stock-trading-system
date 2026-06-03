package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.StockQuoteResponse;
import com.example.stocktradingsystem.entity.Stock;
import com.example.stocktradingsystem.repository.StockRepository;
import com.example.stocktradingsystem.service.MarketDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    // 腾讯行情 API：~ 分隔文本格式，极稳定，支持批量查询 sh/sz 前缀
    private static final String EASTMONEY_URL = "https://qt.gtimg.cn/q={secids}";

    private final StockRepository stockRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.market.cache-key}")
    private String quoteCacheKey;

    // 行情查询：Redis 缓存（10s TTL）→ MySQL 兜底，Redis 不可用时服务不中断
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

    // 定时行情刷新（每5s）：优先拉东方财富实时数据，API 不可用时自动降级到本地模拟
    @Override
    @Scheduled(fixedRateString = "${app.market.refresh-rate-ms}", initialDelay = 10000)
    @Transactional
    public void refreshLatestQuotes() {
        if (stockRepository.count() == 0) {
            initDemoStocks();
        }

        List<Stock> stocks = stockRepository.findAll();
        boolean useFallback = !fetchFromEastMoney(stocks);
        if (useFallback) {
            simulatePrices(stocks);
        }
        stockRepository.saveAll(stocks);
        cacheQuotes(stocks.stream().map(this::toQuoteResponse).toList());
    }

    // 腾讯行情接口：~ 分隔的文本格式
    // 前缀：sh（上海6/9开头）、sz（深圳0/3开头）
    // 批量查询返回多行，每行格式：v_MARKETCODE="...~name~code~price~prevClose~..."
    // 索引：[1]=名称 [2]=代码 [3]=最新价 [4]=昨收
    private boolean fetchFromEastMoney(List<Stock> stocks) {
        try {
            String secids = stocks.stream()
                    .map(s -> {
                        String prefix = s.getCode().startsWith("6") || s.getCode().startsWith("9") ? "sh" : "sz";
                        return prefix + s.getCode();
                    })
                    .collect(java.util.stream.Collectors.joining(","));
            String url = EASTMONEY_URL.replace("{secids}", secids);
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isBlank()) {
                return false;
            }

            for (Stock stock : stocks) {
                String prefix = stock.getCode().startsWith("6") || stock.getCode().startsWith("9") ? "sh" : "sz";
                String marker = "v_" + prefix + stock.getCode() + "=\"";
                int start = response.indexOf(marker);
                if (start < 0) {
                    return false;
                }
                start += marker.length();
                int end = response.indexOf("\"", start);
                if (end < 0) {
                    return false;
                }
                String line = response.substring(start, end);
                String[] parts = line.split("~");
                if (parts.length < 5) {
                    return false;
                }

                BigDecimal price = new BigDecimal(parts[3]);
                BigDecimal prevClose = new BigDecimal(parts[4]);

                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    return false;
                }

                BigDecimal changeRate = price.subtract(prevClose)
                        .multiply(ONE_HUNDRED)
                        .divide(prevClose, 4, RoundingMode.HALF_UP);

                stock.setPreviousClose(prevClose.setScale(4, RoundingMode.HALF_UP));
                stock.setLatestPrice(price.setScale(4, RoundingMode.HALF_UP));
                stock.setChangeRate(changeRate);
                stock.setUpdatedAt(LocalDateTime.now());
            }
            log.info("Tencent quote fetch succeeded for {} stocks", stocks.size());
            return true;
        } catch (Exception e) {
            log.warn("Tencent quote fetch failed, fallback to simulation: {}", e.getMessage());
            return false;
        }
    }

    // 降级方案：当东方财富 API 不可达时，基于当前价格做 ±5% 随机波动
    private void simulatePrices(List<Stock> stocks) {
        LocalDateTime now = LocalDateTime.now();
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

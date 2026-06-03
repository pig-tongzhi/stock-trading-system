package com.example.stocktradingsystem.config;

import com.example.stocktradingsystem.service.MarketDataService;
import com.example.stocktradingsystem.service.StockMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MarketDataService marketDataService;
    private final StockMasterService stockMasterService;

    @Bean
    public CommandLineRunner initMarketData() {
        return args -> {
            stockMasterService.importAll();
            marketDataService.refreshLatestQuotes();
        };
    }
}

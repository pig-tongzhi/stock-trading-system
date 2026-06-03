package com.example.stocktradingsystem.config;

import com.example.stocktradingsystem.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final MarketDataService marketDataService;

    @Bean
    public CommandLineRunner initMarketData() {
        return args -> marketDataService.refreshLatestQuotes();
    }
}

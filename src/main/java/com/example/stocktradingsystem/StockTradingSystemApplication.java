package com.example.stocktradingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StockTradingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTradingSystemApplication.class, args);
    }
}

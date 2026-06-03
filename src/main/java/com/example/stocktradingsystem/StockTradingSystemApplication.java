package com.example.stocktradingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 启动类：Spring Boot + 定时任务
// @EnableScheduling 启用 MarketDataService 每5秒刷新行情
@EnableScheduling
@SpringBootApplication
public class StockTradingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTradingSystemApplication.class, args);
    }
}

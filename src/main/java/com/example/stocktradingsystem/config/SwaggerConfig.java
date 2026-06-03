package com.example.stocktradingsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockTradingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("模拟股票交易系统 API")
                        .version("1.0.0")
                        .description("用于 Java 后端面试展示的模拟股票交易系统接口文档。"));
    }
}

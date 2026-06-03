package com.example.stocktradingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @Column(length = 16)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal latestPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal previousClose;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal changeRate;

    @Column(nullable = false)
    private Boolean tradingEnabled;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

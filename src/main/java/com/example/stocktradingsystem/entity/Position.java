package com.example.stocktradingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "positions",
        uniqueConstraints = @UniqueConstraint(name = "uk_positions_account_stock", columnNames = {"account_id", "stock_code"})
)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "stock_code", nullable = false, length = 16)
    private String stockCode;

    @Column(nullable = false, length = 128)
    private String stockName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal averageCost;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal realizedProfit;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

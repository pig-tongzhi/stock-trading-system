package com.example.stocktradingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMaster {
    @Id
    @Column(length = 16)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;
}

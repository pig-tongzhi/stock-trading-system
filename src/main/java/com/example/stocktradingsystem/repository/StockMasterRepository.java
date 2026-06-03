package com.example.stocktradingsystem.repository;

import com.example.stocktradingsystem.entity.StockMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMasterRepository extends JpaRepository<StockMaster, String> {
    List<StockMaster> findByCodeContainingOrNameContaining(String code, String name);
}

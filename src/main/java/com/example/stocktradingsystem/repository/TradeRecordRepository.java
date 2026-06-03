package com.example.stocktradingsystem.repository;

import com.example.stocktradingsystem.entity.TradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRecordRepository extends JpaRepository<TradeRecord, Long> {

    List<TradeRecord> findByAccountIdOrderByTradedAtDesc(Long accountId);
}

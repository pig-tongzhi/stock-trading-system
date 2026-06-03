package com.example.stocktradingsystem.repository;

import com.example.stocktradingsystem.entity.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {

    List<TradeOrder> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}

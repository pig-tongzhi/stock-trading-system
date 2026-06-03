package com.example.stocktradingsystem.repository;

import com.example.stocktradingsystem.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByAccountIdAndStockCode(Long accountId, String stockCode);

    List<Position> findByAccountIdAndQuantityGreaterThan(Long accountId, Integer quantity);

    List<Position> findByAccountId(Long accountId);
}

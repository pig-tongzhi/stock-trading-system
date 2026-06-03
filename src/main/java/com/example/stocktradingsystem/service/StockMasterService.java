package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.entity.StockMaster;

import java.util.List;

public interface StockMasterService {
    void importAll();
    List<StockMaster> search(String keyword);
    List<StockMaster> listAll();
}

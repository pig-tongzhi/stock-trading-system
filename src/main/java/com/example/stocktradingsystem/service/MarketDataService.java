package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.StockQuoteResponse;

import java.util.List;

public interface MarketDataService {

    List<StockQuoteResponse> listQuotes();

    void refreshLatestQuotes();
}

package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.LeaderboardItemResponse;

import java.util.List;

public interface LeaderboardService {

    List<LeaderboardItemResponse> topProfitUsers(int limit);
}

package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.dto.LeaderboardItemResponse;
import com.example.stocktradingsystem.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ApiResponse<List<LeaderboardItemResponse>> topProfitUsers(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(leaderboardService.topProfitUsers(limit));
    }
}

package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.dto.AssetSummaryResponse;
import com.example.stocktradingsystem.dto.PositionResponse;
import com.example.stocktradingsystem.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public ApiResponse<List<PositionResponse>> listPositions() {
        return ApiResponse.success(positionService.listCurrentPositions());
    }

    @GetMapping("/assets")
    public ApiResponse<AssetSummaryResponse> getAssetSummary() {
        return ApiResponse.success(positionService.getCurrentAssetSummary());
    }
}

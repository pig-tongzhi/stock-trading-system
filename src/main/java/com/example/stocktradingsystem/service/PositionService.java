package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.AssetSummaryResponse;
import com.example.stocktradingsystem.dto.PositionResponse;

import java.util.List;

public interface PositionService {

    List<PositionResponse> listCurrentPositions();

    AssetSummaryResponse getCurrentAssetSummary();
}

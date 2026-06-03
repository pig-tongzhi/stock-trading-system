package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.entity.StockMaster;
import com.example.stocktradingsystem.service.StockMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockMasterController {

    private final StockMasterService stockMasterService;

    @GetMapping("/search")
    public ApiResponse<List<StockMaster>> search(@RequestParam String q) {
        return ApiResponse.success(stockMasterService.search(q));
    }

    @GetMapping("/all")
    public ApiResponse<List<StockMaster>> all() {
        return ApiResponse.success(stockMasterService.listAll());
    }
}

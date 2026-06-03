package com.example.stocktradingsystem.controller;

import com.example.stocktradingsystem.dto.AccountCreateRequest;
import com.example.stocktradingsystem.dto.AccountResponse;
import com.example.stocktradingsystem.dto.ApiResponse;
import com.example.stocktradingsystem.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }

    @GetMapping("/me")
    public ApiResponse<AccountResponse> getCurrentAccount() {
        return ApiResponse.success(accountService.getCurrentAccount());
    }
}

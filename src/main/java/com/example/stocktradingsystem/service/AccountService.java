package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.AccountCreateRequest;
import com.example.stocktradingsystem.dto.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(AccountCreateRequest request);

    AccountResponse getCurrentAccount();
}

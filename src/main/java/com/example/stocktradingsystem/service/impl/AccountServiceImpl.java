package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.AccountCreateRequest;
import com.example.stocktradingsystem.dto.AccountResponse;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.exception.BusinessException;
import com.example.stocktradingsystem.repository.AccountRepository;
import com.example.stocktradingsystem.security.CurrentUserService;
import com.example.stocktradingsystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        Long userId = currentUserService.currentUser().userId();
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Account already exists.");
        }
        if (request.initialBalance() == null || request.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Initial balance must be greater than or equal to zero.");
        }

        LocalDateTime now = LocalDateTime.now();
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountName(request.accountName());
        account.setAvailableBalance(request.initialBalance());
        account.setFrozenBalance(BigDecimal.ZERO);
        account.setInitialAsset(request.initialBalance());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        return toResponse(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getCurrentAccount() {
        Account account = accountRepository.findByUserId(currentUserService.currentUser().userId())
                .orElseThrow(() -> new BusinessException("Account not found."));
        return toResponse(account);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getAccountName(),
                account.getAvailableBalance(),
                account.getFrozenBalance(),
                account.getInitialAsset(),
                account.getCreatedAt()
        );
    }
}

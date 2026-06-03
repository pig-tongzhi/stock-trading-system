package com.example.stocktradingsystem.service.impl;

import com.example.stocktradingsystem.dto.AccountResponse;
import com.example.stocktradingsystem.dto.AuthResponse;
import com.example.stocktradingsystem.dto.LoginRequest;
import com.example.stocktradingsystem.dto.RegisterRequest;
import com.example.stocktradingsystem.entity.Account;
import com.example.stocktradingsystem.entity.AppUser;
import com.example.stocktradingsystem.exception.BusinessException;
import com.example.stocktradingsystem.repository.AccountRepository;
import com.example.stocktradingsystem.repository.AppUserRepository;
import com.example.stocktradingsystem.security.JwtService;
import com.example.stocktradingsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists.");
        }

        LocalDateTime now = LocalDateTime.now();
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setRole("USER");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        AppUser savedUser = appUserRepository.save(user);

        Account account = new Account();
        account.setUserId(savedUser.getId());
        account.setAccountName(savedUser.getNickname() + "的模拟交易账户");
        account.setAvailableBalance(request.initialBalance());
        account.setFrozenBalance(BigDecimal.ZERO);
        account.setInitialAsset(request.initialBalance());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        Account savedAccount = accountRepository.save(account);

        return buildResponse(savedUser, savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Invalid username or password.");
        }
        Account account = accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Account not found."));
        return buildResponse(user, account);
    }

    private AuthResponse buildResponse(AppUser user, Account account) {
        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                toAccountResponse(account)
        );
    }

    private AccountResponse toAccountResponse(Account account) {
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

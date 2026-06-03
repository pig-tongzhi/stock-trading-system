package com.example.stocktradingsystem.service;

import com.example.stocktradingsystem.dto.AuthResponse;
import com.example.stocktradingsystem.dto.LoginRequest;
import com.example.stocktradingsystem.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

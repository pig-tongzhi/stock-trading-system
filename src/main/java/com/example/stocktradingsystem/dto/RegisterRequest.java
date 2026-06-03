package com.example.stocktradingsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 64)
        String username,
        @NotBlank
        @Size(min = 6, max = 64)
        String password,
        @NotBlank
        String nickname,
        @NotNull
        @DecimalMin(value = "1000.00")
        BigDecimal initialBalance
) {
}

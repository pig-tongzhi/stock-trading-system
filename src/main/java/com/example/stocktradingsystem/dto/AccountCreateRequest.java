package com.example.stocktradingsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountCreateRequest(
        @NotBlank
        String accountName,
        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal initialBalance
) {
}

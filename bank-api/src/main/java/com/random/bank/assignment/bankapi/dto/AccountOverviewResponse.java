package com.random.bank.assignment.bankapi.dto;

import java.math.BigDecimal;

public record AccountOverviewResponse(
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String currency
) {
}

package com.random.bank.assignment.bankapi.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        String username,

        @NotBlank
        String password
) {
}

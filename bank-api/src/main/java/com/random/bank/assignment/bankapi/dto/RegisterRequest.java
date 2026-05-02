package com.random.bank.assignment.bankapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegisterRequest(

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotBlank
        String username,

        @NotNull
        LocalDate dateOfBirth,

        @NotBlank
        String country
) {
}
